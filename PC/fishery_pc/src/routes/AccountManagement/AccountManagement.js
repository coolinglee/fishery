import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, message } from 'antd';
import update from 'immutability-helper'
const { TextArea } = Input;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { access } from 'fs';
export default class CompanyUserDetail extends React.Component {

    constructor (props) {
        super(props);
        this.state = {
            originalAccount: {
                name: "杨威",
                account: "0001",
                password: "123456",
                remark: "我就瞎备注一下"
            },
            accountInfo: {
                name: "杨威",
                account: "0001",
                password: "123456",
                passwordAgain: "123456",
                remark: "我就瞎备注一下"
            }
        }
    }
    handleOk = () => {
        const account = this.state.accountInfo;
        if(!account.password) {
            message.warn("请输入密码！");
            return;
        } else if (!account.passwordAgain){
            message.warn("请再次输入密码！");
            return;
        } else if (!(account.password == account.passwordAgain)){
            message.warn("两次输入的密码不相同，请重新输入");
            // this.setState({
            //     passwordAgain : ""
            // })
            return;
        } else {
            const accountInfo = this.state.accountInfo;
            this.setState({
                originalAccount: accountInfo
            })
            message.success("修改成功！");
        }
        
    }

    handleCancal = () => {
        const originalAccount = this.state.originalAccount;
        originalAccount.passwordAgain = originalAccount.password;
        this.setState({
            accountInfo: originalAccount
        });
        console.log("handleCancal");
    }

    handleInputChange = (value, key) => {
        if(key) {
            this.setState({
                accountInfo: update(this.state.accountInfo, { [key]: { $set: value } })
            })
        }
    }

    render() {
        return (
            <PageHeaderLayout>
                <Card>
                    <Row style={{height: 60, fontSize: 20}} align="middle">
                        <Col span={3} offset={7}>
                            账号：
                       </Col>
                       <Col span={6}>
                       {this.state.accountInfo.account}
                       </Col>
                    </Row>
                    <Row style={{height: 60, fontSize:20}} align="middle">
                        <Col span={3} offset={7}>
                            名称：
                       </Col>
                       <Col span={6} >
                            {this.state.accountInfo.name}
                       </Col>
                    </Row>
                    <Row style={{height: 60, fontSize: 20}} align="middle">
                        <Col span={3} offset={7}>
                            密码：
                       </Col>
                       <Col span={6} >
                            <Input type="password" value={this.state.accountInfo.password} onChange={e => this.handleInputChange(e.target.value, "password")}/>
                       </Col>
                    </Row>
                    <Row style={{height: 60, fontSize: 20}} align="middle">
                        <Col span={3} offset={7}>
                            密码确认：
                       </Col>
                       <Col span={6}>
                            <Input type="password" value={this.state.accountInfo.passwordAgain} onChange={e => this.handleInputChange(e.target.value, "passwordAgain")}/>
                       </Col>
                    </Row>
                    <Row style={{height: 150, fontSize: 20}} align="middle">
                        <Col span={3} offset={7}>
                            备注：
                        </Col>
                        <Col span={6} >
                            <TextArea rows={3} value={this.state.accountInfo.remark} onChange={e => this.handleInputChange(e.target.value, "remark")}/>
                        </Col>
                    </Row>
                    <Row style={{height: 60, fontSize: 20}} align="middle">
                        <Col span={3} offset={8}>
                            <Button size="large" onClick={this.handleCancal}>取消</Button>
                        </Col>
                        <Col span={3} offset={2}>
                            <Button type="primary" size="large" onClick={this.handleOk}>确定</Button>
                        </Col>
                    </Row>
                </Card>
            </PageHeaderLayout>
        );
    }
}