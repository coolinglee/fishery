
import { List, InputItem, Button, DatePicker, Modal, Toast } from 'antd-mobile';
import { createForm } from 'rc-form';
import NavBar from '../../components/NavBar';
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import addImg from '../../img/add.png'
import question from '../../img/question.png'
import './autoOxygenationSetting.less';
import { delSensorOrAIOBind, delBind } from '../../services/bind.js'; //接口
const Item = List.Item;
const CustomChildren = ({ extra, onClick, children }) => (
    <div
      onClick={onClick}
      style={{ backgroundColor: '#fff', height: '37px', lineHeight: '37px', padding: '0 0' }}
    >
      {children}
      <span style={{ float: 'right', color: '#888' }}>{extra}</span>
    </div>
  );
class AutoOxygenationSetting extends React.Component {

    state = {
        time: '',
        timeSections: [['', '']],
        isShowDeclare: false
    }

    onSubmit = () => {
        if(timeSections.length <= 0) {
            Toast.fail('必须选择至少一个时间段！', 1);
            return;
        }
        const timeSections = this.state.timeSections;
        for (let i = 0; i < timeSections.length; i++) {
            if (!timeSection[i][0] || !timeSection[i][0]) {
                Toast.fail('有未填写完整的时间段，请填写后提交！', 1);
                    return;
            }
            for (let j = i + 1; j < timeSections.length; j++) {
                if (timeSections[i][0] < timeSections[j][0] && timeSections[i][1] > timeSections[j][0] || 
                    timeSections[j][0] < timeSections[i][0] && timeSections[j][1] > timeSections[i][0] 
                ) {
                    Toast.fail('时间段不能交错，请修改后提交！', 1);
                    return;
                }
           }
        }
        this.props.form.validateFields({ force: true }, (error) => {
            if (!error) {
                console.log(this.props.form.getFieldsValue());
            } else {
                alert('Validation failed');
            }
        });
    }

    onReset = () => {
        this.props.form.resetFields();
    }

    addTimeSection = () => {
        console.log('add')
        timeSections.push(['', '']);
        this.setState({
            timeSections: timeSections
        })
    }

    handleTimeChange = (time, index, aryIndex) => {
        let timeSections = this.state.timeSections;
        let timeSection = timeSections[index];

        if (aryIndex == 0) {
            if(timeSection[1]) {
                if(timeSection[1] < time) {
                    Toast.fail('开始时间不能大于等于结束时间', 1);
                    return;
                }
            }
        } else if (aryIndex == 1) {
            if(timeSection[0]) {
                if(timeSection[0] > time) {
                    Toast.fail('开始时间不能大于等于结束时间', 1);
                    return;
                }
            }
        }
        timeSection[aryIndex] = time;
        this.setState({timeSections: timeSections});
    }
    render() {
        const { getFieldProps, getFieldError } = this.props.form;
        const timeSections = this.state.timeSections;
        const times = timeSections.map((item, index) => {
            return <Item className='timeItem' key={index} extra={<div className='time' >
                <DatePicker
                    mode="time"
                    minuteStep={5}
                    value={item[0]}
                    onChange={time => this.handleTimeChange(time, index, 0 )}
                    extra='开始时间'
                    className=''
                >
                    {/* <span>开始时间&nbsp;</span> */}
                    <CustomChildren></CustomChildren>
                </DatePicker>
                -
                    <DatePicker
                    mode="time"
                    minuteStep={5}
                    value={item[1]}
                    extra='结束时间'
                    onChange={time => this.handleTimeChange(time, index, 1 )}
                    className=''
                >
                    {/* <span>&nbsp;结束时间</span> */}
                    <CustomChildren></CustomChildren>
                </DatePicker>
            </div>}>定时增氧</Item>
        })
        return (<form className='oxygen-set-bg' >
            <div className="nav-bar-title">
                <i className="back" onClick={() => {
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'right'
                        }
                    });
                    history.back();
                }}></i>
                设置自动增氧
                <i className="right-item-none" onClick={() => { this.setState({ isShowDeclare: true }) }} ><img src={question} style={{ width: '.44rem', verticalAlign: 'middle' }} /></i>
            </div>
            <Modal
                visible={this.state.isShowDeclare}
                transparent
                maskClosable={true}
                onClose={() => { this.setState({ isShowDeclare: false }) }}
                title="说明"
                footer={[{ text: '知道了', onPress: () => { this.setState({ isShowDeclare: false }) } }]}
            >
                <div style={{ height: 100, textAlign: 'left' }}>
                    1.下限（低于下限自动开启增氧）；<br />
                    2.上限（达到上限停止增氧）；<br />
                    3.高限（高于高限时增氧2小时）；<br />
                    4.定时增氧（设定时段，定时强制增氧）；<br />
                </div>
            </Modal>
            <List className='os-list'>
                <Item extra={<span style={{ minWidth: '100px', color: '#000' }} >10.26mg/L</span>}>控制器1<span>(增氧机1)</span></Item>
                <InputItem
                    {...getFieldProps('floor', {
                        rules: [
                            { required: true }
                        ],
                    }) }
                    className="os-input"
                    error={!!getFieldError('floor')}
                    extra={<span>mg/L</span>}
                >增氧下限</InputItem>
                <InputItem
                    {...getFieldProps('upperLimit', {
                        rules: [
                            { required: true }
                        ],
                    }) }
                    className="os-input"
                    error={!!getFieldError('upperLimit')}
                    extra={<span>mg/L</span>}
                >增氧上限</InputItem>
                <InputItem
                    {...getFieldProps('heightLimit', {
                        rules: [
                            { required: true }
                        ],
                    }) }
                    className="os-input"
                    error={!!getFieldError('heightLimit')}
                    extra={<span>mg/L</span>}
                >增氧高限</InputItem>
                {times}
            </List>
            <div className='add-block' onClick={this.addTimeSection} >
                <span className='add-span'>定时</span><img className='add-img' src={addImg} />
            </div>
            <div className='buttons'>
                <div className='left-button'>
                    从设备获得
                </div>
                <div className='right-button' onClick={this.onSubmit}>
                    设置到设备
                </div>
            </div>
        </form>);
    }
}

const AutoOxygenationSettingForm = createForm()(AutoOxygenationSetting);
export default connect()(AutoOxygenationSettingForm);
