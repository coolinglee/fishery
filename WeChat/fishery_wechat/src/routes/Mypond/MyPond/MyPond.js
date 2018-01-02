import React from 'react';
import { List, InputItem, Picker, ActionSheet } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
import { connect } from 'dva';
import './Mypond.less';

class MyPond extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            edit: false
        }
    }
    showActionSheet = () => {
        const BUTTONS = ['删除', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            // title: 'title',
            message: '您是否确定删除该塘口？',
            maskClosable: true,
            'data-seed': 'mypond',
            // wrapProps,
        },
            (buttonIndex) => {
                console.log(buttonIndex)
                this.setState({ clicked: BUTTONS[buttonIndex], edit: !this.state.edit });
            });
    }
    render() {
        return (
            <div className="body-bac">
                <div className="nav-bar-title" >
                    <i className="back" onClick={() => {
                        history.back();
                        this.props.dispatch({
                            type: 'global/changeState',
                            payload: {
                                transitionName: 'right'
                            }
                        })
                    }}></i>
                    我的塘口
                    <i className={this.state.edit?'right-item-none':'edit'} onClick={() => { this.setState({ edit: !this.state.edit }) }}></i>
                </div>
                <div className="mypond-bac"></div>
                <div className="mypond-content">
                    <div>
                        <div className="content-title">小鱼塘</div>
                        <div>
                            <span className="content-info">
                                <i className="content-info-img area-img" />
                                <span className="content-info-value">
                                    20亩
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img address-img" />
                                <span className="content-info-value">
                                    南京玄武区鱼儿乐…
                            </span>
                            </span>
                        </div>
                        <div>
                            <span className="content-info">
                                <i className="content-info-img waterDepth-img" />
                                <span className="content-info-value">
                                    500m
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolFish-img" />
                                <span className="content-info-value">
                                    鲫鱼，草鱼...
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolWater-img" />
                                <span className="content-info-value">
                                    淡水
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolThickness-img" />
                                <span className="content-info-value">
                                    50cm
                            </span>
                            </span>
                        </div>
                    </div>
                    {this.state.edit && <div className="mypond-delete" onClick={() => { this.showActionSheet() }}>
                        <img src={require('../../../img/btn_remove.png')} />
                    </div>}
                </div>
                <div className="mypond-content">
                    <div>
                        <div className="content-title">小鱼塘</div>
                        <div>
                            <span className="content-info">
                                <i className="content-info-img area-img" />
                                <span className="content-info-value">
                                    20亩
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img address-img" />
                                <span className="content-info-value">
                                    南京玄武区鱼儿乐…
                            </span>
                            </span>
                        </div>
                        <div>
                            <span className="content-info">
                                <i className="content-info-img waterDepth-img" />
                                <span className="content-info-value">
                                    500m
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolFish-img" />
                                <span className="content-info-value">
                                    鲫鱼，草鱼...
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolWater-img" />
                                <span className="content-info-value">
                                    淡水
                            </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolThickness-img" />
                                <span className="content-info-value">
                                    50cm
                            </span>
                            </span>
                        </div>
                    </div>
                    {this.state.edit && <div className="mypond-delete" onClick={() => { this.showActionSheet() }}>
                        <img src={require('../../../img/btn_remove.png')} />
                    </div>}
                </div>
                <div className="addPond-btn">取消</div>
                <div className="btn_add" onClick={() => {
                    this.props.history.push('/addPond'); this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'fade'
                        }
                    })
                }}>
                </div>
            </div>
        );
    }

}

export default connect()(MyPond);