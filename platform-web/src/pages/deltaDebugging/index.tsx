import { PageContainer } from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import { Button, Card, Descriptions, Drawer, Typography } from 'antd';
import React, { useState } from 'react';
import DeltaDebuggingStepFlow from './components/ddStepFlow';
import DeltaDebuggingHunkBlocks from './components/ddHunkBlocks';
import { ddResult } from './components/mockData';
import { ddResultItems } from './data';
import DeltaDebuggingHunkRelationGraph from './components/ddHunkRelationGraph';

const InteractiveDeltaDebuggingPage: React.FC<{ ddResult: ddResultItems }> = () => {
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
                  {ddResult.info.projectFullName}
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
                <Typography.Text>{ddResult.info.regressionUuid}</Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item label={'revision'} labelStyle={{ fontWeight: 'bold' }}>
                {ddResult.info.revision}
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
      <div style={{ display: 'flex', marginBottom: 10 }}>
        <Card
          title={
            <div>
              <Button onClick={handleRunDD}>Run</Button>
              <Button onClick={handleRunDD}>Step</Button>
            </div>
          }
          headStyle={{ height: 85 }}
          bodyStyle={{ height: 600 }}
          bordered
          style={{ width: '60%', overflow: 'auto' }}
        >
          <div>
            <DeltaDebuggingStepFlow ddSteps={ddResult}></DeltaDebuggingStepFlow>
          </div>
        </Card>
        <Card
          title={<div>choosed hunks</div>}
          headStyle={{ height: 85 }}
          bodyStyle={{ height: 600 }}
          bordered
          style={{ width: '40%', overflow: 'auto' }}
        >
          <DeltaDebuggingHunkBlocks hunkInfo={ddResult.info}></DeltaDebuggingHunkBlocks>
        </Card>
      </div>
      <div style={{ display: 'flex' }}>
        <Card
          title={'Hunk Relation Graph'}
          headStyle={{ height: 85 }}
          bodyStyle={{ height: 300 }}
          bordered
          style={{ width: '100%', overflow: 'auto' }}
        >
          <DeltaDebuggingHunkRelationGraph />
        </Card>
      </div>
      <Drawer
        // bodyStyle={DrawerbodyStyle}
        title="Regressions List"
        placement={'right'}
        onClose={() => setSidebarRegressionMenu(false)}
        visible={sidebarRegressionMenu}
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
