import { FormattedMessage, Link, SelectLang, useIntl, history } from 'umi';
import { Alert, Tabs, Image } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import ProForm, { ProFormText } from '@ant-design/pro-form';
import Footer from '@/components/Footer';
import { useState } from 'react';

import styles from './style.less';
import { register } from '@/services/ant-design-pro/register';

const loginPath = '/user/login';

const RegisterMessage: React.FC<{
  content: string;
}> = ({ content }) => (
  <Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
);

const goto = () => {
  if (!history) return;
  setTimeout(() => {
    history.push('/user/login');
  }, 10);
};

const RegisterPage: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);
  const [status, setStatus] = useState('');

  const intl = useIntl();

  const handleSubmit = async (values: API.RegisterParams) => {
    setSubmitting(true);
    console.log(values);
    await register({ ...values }).then((resp) => {
      if (typeof resp === 'number') {
        setStatus('error');
        return;
      } else {
        goto();
      }
    });
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
            initialValues={{
              autoLogin: false,
            }}
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({
                  id: 'pages.login.registerAccount',
                  defaultMessage: 'Register',
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
              handleSubmit(values as API.RegisterParams);
            }}
          >
            <Tabs activeKey="register">
              <Tabs.TabPane
                key="register"
                tab={intl.formatMessage({
                  id: 'pages.login.registerAccount.tab',
                  defaultMessage: 'Register Account',
                })}
              />
            </Tabs>
            {status === 'error' && (
              <RegisterMessage
                content={intl.formatMessage({
                  id: 'pages.login.register.errorMessage',
                  defaultMessage: 'User name already exist, try a new one!',
                })}
              />
            )}
            <ProFormText
              name="accountName"
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
                id: 'pages.login.password.placeholder',
                defaultMessage: 'Password: ',
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
            <ProFormText
              name="email"
              fieldProps={{
                size: 'large',
                prefix: <MailOutlined className={styles.prefixIcon} />,
              }}
              placeholder={intl.formatMessage({
                id: 'app.settings.basic.email',
                defaultMessage: 'Email',
              })}
              rules={[
                {
                  required: false,
                },
              ]}
            />
            {/* <ProFormSelect
              name="role"
              fieldProps={{
                size: 'large',
              }}
              options={[
                {
                  value: 'user',
                  label: 'Normal user',
                },
                {
                  value: 'admin',
                  label: 'Administrater',
                },
              ]}
              placeholder={intl.formatMessage({
                id: 'pages.login.register.select-role',
                defaultMessage: 'Select your role',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.register.role.required"
                      defaultMessage="Please select your role!"
                    />
                  ),
                },
              ]}
            /> */}
            {/* <ProFormUploadButton
              name="avatar"
              extra="only support .jpg .png files"
              title={intl.formatMessage({
                id: 'app.settings.basic.upload-avatar',
                defaultMessage: 'Upload avatar',
              })}
              rules={[
                {
                  required: false,
                },
              ]}
            /> */}
          </ProForm>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default RegisterPage;
