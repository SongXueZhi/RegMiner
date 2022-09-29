import { resetPassword } from '@/services/ant-design-pro/login';
import { UserOutlined, LockOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import ProForm, { ProFormText } from '@ant-design/pro-form';
import { Tabs, Image } from 'antd';
import { Footer } from 'antd/lib/layout/layout';
import { useState } from 'react';
import { SelectLang, Link, FormattedMessage, useIntl, history } from 'umi';

import styles from './style.less';

const loginPath = '/user/login';

const ResetPassword: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);

  const intl = useIntl();

  const handleSubmit = async (values: API.ResetPasswordParams) => {
    setSubmitting(true);
    await resetPassword({ ...values });
    history.push('/user/login');
    setSubmitting(false);
  };

  return (
    <div className={styles.container}>
      <div className={styles.lang}>{SelectLang && <SelectLang />}</div>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            <Link to="/">
              <Image alt="logo" src="/favicon.ico" className={styles.logo} />
              <span className={styles.title}>RegMiner Data Annotations</span>
            </Link>
          </div>
          <div className={styles.desc}>RegMiner 数据标注平台</div>
        </div>
        <div className={styles.main}>
          <a href={loginPath}>
            <ArrowLeftOutlined className={styles.prefixIcon} />
          </a>
          <ProForm
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({
                  id: 'pages.login.resetPassword',
                  defaultMessage: 'Reset Password',
                }),
              },
              render: (_, dom) => dom.pop(),
              submitButtonProps: {
                loading: submitting,
                size: 'large',
                style: {
                  width: '100%',
                },
              },
            }}
            onFinish={async (values) => {
              handleSubmit(values as API.ResetPasswordParams);
            }}
          >
            <Tabs activeKey="account">
              <Tabs.TabPane
                key="account"
                tab={intl.formatMessage({
                  id: 'pages.login.resetPassword.tab',
                  defaultMessage: 'Reset',
                })}
              />
            </Tabs>
            <ProFormText
              name="account_name"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined className={styles.prefixIcon} />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.login.username.placeholder',
                defaultMessage: 'Username',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.username.required"
                      defaultMessage="Please input your username!"
                    />
                  ),
                },
              ]}
            />
            <ProFormText.Password
              name="password"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined className={styles.prefixIcon} />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.login.password.new.placeholder',
                defaultMessage: 'New Password',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.password.required"
                      defaultMessage="Please input your password!"
                    />
                  ),
                },
              ]}
            />
          </ProForm>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ResetPassword;
