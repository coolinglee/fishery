import { queryFakeList, addUser } from '../services/api';
import { queryPond, addPond, modifyPond, delPonds } from '../services/pond'
import update from 'immutability-helper'

export default {
    namespace: 'pond',

    state: {
        list: [],
        loading: false,
        pagination: { current: 1 },
        modalvisible: false,
        mapVisible: false,
        address: '',
        formData: { fields: {} }
    },

    effects: {
        *fetch({ payload }, { call, put }) {
            yield put({
                type: 'changeLoading',
                payload: true,
            });
            const response = yield call(queryPond, payload);
            console.log(response)
            if (response.code == "0") {
                for (let item of response.data) {
                    item.key = item.id
                }
                yield put({
                    type: 'appendList',
                    payload: {
                        list: Array.isArray(response.data) ? response.data : [],
                        pagination: {
                            total: response.realSize,
                        }
                    }
                });
            }
            yield put({
                type: 'changeLoading',
                payload: false,
            });
        },
        *addPond({ payload }, { call, put }) {
            const response = yield call(addPond, payload);
            if (response.code == '0') {
                yield put({
                    type: 'addList',
                    payload: response.data,
                });
            }
        },
        *modifyPond({ payload }, { call, put }) {
            const response = yield call(modifyPond, payload.data);
            if (response.code == '0') {
                yield put({
                    type: 'modifyList',
                    payload: {
                        index: payload.index,
                        data: response.data,
                    },
                });
            }
        },
        *deletePond({ payload }, { call, put }) {
            const response = yield call(delPonds, { pondIds: payload.pondIds });
            if (response.code == '0') {
                yield put({
                    type: 'fetch',
                    payload: {
                        page: payload.pagination.current,
                        number: 10
                    }
                })
            }
        }
    },

    reducers: {
        appendList(state, action) {
            return {
                ...state,
                list: action.payload.list,
                pagination: { ...state.pagination, ...action.payload.pagination }
            };
        },
        addList(state, action) {
            let list = state.list;
            list.unshift(action.payload);
            return {
                ...state,
                list: list,
                pagination: { ...state.pagination, total: state.pagination.total + 1 }
            };
        },
        modifyList(state, action) {
            return {
                ...state,
                list: update(state.list, { [action.payload.index]: { $set: action.payload.data } })
            }
        },
        changeLoading(state, action) {
            return {
                ...state,
                loading: action.payload,
            };
        },
        changeModal(state, action) {
            return { ...state, ...action.payload };
        },
    },
};
