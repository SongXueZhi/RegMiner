import { PageContainer } from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import { Button, Card, Descriptions, Divider, Drawer, Typography } from 'antd';
import React, { useState } from 'react';
import { ddResult } from './components/mockData';

const InteractiveDeltaDebuggingPage: React.FC<{}> = () => {
  const [sidebarRegressionMenu, setSidebarRegressionMenu] = useState<boolean>(false);

  const handleRunDD = () => {
    console.log('RUN');
  };

  return (
    <PageContainer
      header={{
        style: { width: '100%' },
        title: 'Interactive Delta Debuging',
        subTitle: (
          <div className="sub-title-container">
            <div>
              <Button
                className="sub-title-header"
                type="primary"
                onClick={() => setSidebarRegressionMenu(true)}
              >
                Show Regressions
              </Button>
            </div>
          </div>
        ),
        footer: (
          <div style={{ display: 'inline-flex', alignItems: 'center' }}>
            <Descriptions column={3} style={{ flex: 1 }}>
              <Descriptions.Item label={'Project'} labelStyle={{ fontWeight: 'bold' }}>
                <Typography.Text keyboard strong>
                  projectFullName
                </Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item label={'Bug Inducing Commit'} labelStyle={{ fontWeight: 'bold' }}>
                {/* <Typography.Link keyboard href={BICURL} target="_blank">
                      {BIC?.slice(0, 8)}...
                    </Typography.Link> */}
                <br />
              </Descriptions.Item>
              <Descriptions.Item label={'Bug Fixing Commit'} labelStyle={{ fontWeight: 'bold' }}>
                {/* <Typography.Link keyboard href={BFCURL} target="_blank">
                      {BFC?.slice(0, 8)}...
                    </Typography.Link> */}
                <br />
              </Descriptions.Item>
              <Descriptions.Item label={'Regression UUID'} labelStyle={{ fontWeight: 'bold' }}>
                <Typography.Text>regUuidxxx-xxx-xx</Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item
                label={'Regression description'}
                labelStyle={{ fontWeight: 'bold' }}
              >
                <Typography.Text>regressionDescription</Typography.Text>
              </Descriptions.Item>
            </Descriptions>
          </div>
        ),
      }}
    >
      <div style={{ display: 'flex' }}>
        <Card bordered style={{ marginRight: 10, width: '50%', overflow: 'auto' }}>
          <div>
            <Button onClick={handleRunDD}>Run</Button>
            <Button onClick={handleRunDD}>Run</Button>
          </div>
          <Divider />
        </Card>
        <Card bordered style={{ width: '50%', overflow: 'auto' }}></Card>
      </div>
      <Drawer
        // bodyStyle={DrawerbodyStyle}
        title="Regressions List"
        placement={'right'}
        onClose={() => setSidebarRegressionMenu(false)}
        open={sidebarRegressionMenu}
        key={'right'}
        width={450}
      >
        <ProTable<API.RegressionItem>
          headerTitle="Bugs"
          //   actionRef={actionRef}
          rowKey="regressionUuid"
          search={false}
        />
      </Drawer>
    </PageContainer>
  );
};

export default InteractiveDeltaDebuggingPage;
