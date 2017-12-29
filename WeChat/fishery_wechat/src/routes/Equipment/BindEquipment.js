import React from 'react';
import './bindEquipment.less'
import { Picker, List } from 'antd-mobile'
import { createForm } from 'rc-form';
import { withRouter } from "react-router-dom";
import NavBar from '../../components/NavBar';
const testData = [
    'a',
    'b'
]
const seasons = [
    [
        {
            label: '2013',
            value: '2013',
        },
        {
            label: '2014',
            value: '2014',
        },
    ],
    [
        {
            label: '春',
            value: '春',
        },
        {
            label: '夏',
            value: '夏',
        },
    ],
];
class BindEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {

        }
    }

    render() {
        return <div className='bind-equipment-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"设备绑定"} />
            <div className='header-line' >
                <div className='left'>
                    端口名称
                </div>
                <div className='name' >
                    端口1
                </div>
            </div>
            <div className='bind-info' >
                <div className='line' >
                    <div className='left'>绑定设备：</div>
                    <div className='right'>
                        设备
                    </div>
                </div>
                <div className='line' >
                    <div className='left'>绑定端口：</div>
                    {/* <Picker
                        cols={1}
                        data={seasons}
                        > */}
                    <div className='right'>
                        设备
                    </div>
                    {/* </Picker> */}
                </div>
                <List>
                    <Picker data={testData} cols={1} className="forss">
                        <List.Item arrow="horizontal" key='1'>Single</List.Item>
                    </Picker>
                </List>
            </div>

            <div className='save-button' onClick={() => { this.saveInfo() }} >
                保  存
            </div>
        </div>
    }
}

export default withRouter(BindEquipment);
