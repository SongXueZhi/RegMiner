import { Space } from 'antd';
import React from 'react';
import { SelectLang, useModel } from 'umi';
import AvatarDropdown from './AvatarDropdown';
import styles from './index.less';

export type SiderTheme = 'light' | 'dark';

const GlobalHeaderRight: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  // const [visible, setVisible] = useState<boolean>();

  if (!initialState || !initialState.settings) {
    return null;
  }

  const { navTheme, layout } = initialState.settings;
  let className = styles.right;

  if ((navTheme === 'dark' && layout === 'top') || layout === 'mix') {
    className = `${styles.right}  ${styles.dark}`;
  }

  // 计时器
  // function wait(ms: number) {
  //   return new Promise((resolve) => {
  //     setTimeout(() => {
  //       console.log('running');
  //       setVisible(false);
  //       resolve(true);
  //     }, ms);
  //   });
  // }
  // wait(120000);
  return (
    <Space className={className}>
      {/* <HeaderSearch
        className={`${styles.action} ${styles.search}`}
        placeholder="站内搜索"
        defaultValue="umi ui"
        options={[
          { label: <a href="https://umijs.org/zh/guide/umi-ui.html">umi ui</a>, value: 'umi ui' },
          {
            label: <a href="next.ant.design">Ant Design</a>,
            value: 'Ant Design',
          },
          {
            label: <a href="https://protable.ant.design/">Pro Table</a>,
            value: 'Pro Table',
          },
          {
            label: <a href="https://prolayout.ant.design/">Pro Layout</a>,
            value: 'Pro Layout',
          },
        ]}
        // onSearch={value => {
        //   console.log('input', value);
        // }}
      />
      <span
        className={styles.action}
        onClick={() => {
          window.open('https://pro.ant.design/docs/getting-started');
        }}
      >
        <QuestionCircleOutlined />
      </span> */}

      {/* <Popover
        trigger="hover"
        color="#ffffb8"
        placement="leftTop"
        defaultVisible={true}
        visible={visible}
        style={{ marginRight: '20px' }}
        title={
          <Space align="center">
            <BulbOutlined />
            <span>Check this out!</span>
          </Space>
        }
        content={
          <Space direction="vertical">
            <div>Watch this tutorial video to</div>
            <div>get familiar with RegMiner</div>
          </Space>
        }
      >
        <Button
          className="tutorial-link-youtube"
          type="link"
          href="https://youtu.be/QtqS8f2yApc"
          icon={<YoutubeOutlined />}
          target="_blank"
          size="middle"
          style={{ backgroundColor: 'red', color: 'white', marginRight: '10px' }}
        >
          Tutorial
        </Button>
      </Popover> */}
      <AvatarDropdown />
      <SelectLang className={styles.action} />
    </Space>
  );
};
export default GlobalHeaderRight;
