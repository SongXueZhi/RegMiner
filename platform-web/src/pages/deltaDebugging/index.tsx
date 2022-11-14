import { PageContainer } from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import { Button, Card, Descriptions, Drawer, Steps, Typography } from 'antd';
import React, { useState } from 'react';
import DeltaDebuggingHunkBlocks from './components/ddHunkBlocks';
import { ddResult } from './components/mockData';
import { ddResultItems, ddStepsItems } from './data';
import DeltaDebuggingHunkRelationGraph from './components/ddHunkRelationGraph';
import DeltaDebuggingStepResultTable from './components/ddStepResultTable';
import ProCard from '@ant-design/pro-card';

const InteractiveDeltaDebuggingPage: React.FC<{ ddResult: ddResultItems }> = () => {
  const [sidebarRegressionMenu, setSidebarRegressionMenu] = useState<boolean>(false);
  const [current, setCurrent] = useState<number>(0);
  const [selectedStepInfo, setSelectedStepInfo] = useState<ddStepsItems[]>([]);

  const handleRunDD = () => {
    console.log('RUN');
  };

  const handleStepsChange = (value: number) => {
    // console.log('change', current);
    setCurrent(value);
    selectedStepInfo.push(ddResult.steps[value]);
    setSelectedStepInfo(selectedStepInfo);
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
          style={{ width: '30%', overflow: 'auto' }}
        >
          <Steps current={current} onChange={handleStepsChange} direction="vertical">
            {ddResult.steps.map((resp) => {
              return (
                <Steps.Step
                  key={resp.stepNum}
                  status={
                    resp.testResult === 'failed'
                      ? 'error'
                      : resp.testResult === 'CE'
                      ? 'process'
                      : resp.testResult === 'pass'
                      ? 'finish'
                      : 'process'
                  }
                  title={`Step result: ${resp.testResult}`}
                  description={`Tested hunks: [${resp.testedHunks}]`}
                />
              );
            })}
          </Steps>
        </Card>
        <ProCard
          title={<div>choosed hunks</div>}
          headStyle={{ height: 85 }}
          bodyStyle={{ height: 600 }}
          bordered
          style={{ width: '70%', overflow: 'auto' }}
          split={'horizontal'}
        >
          <ProCard split={'horizontal'}>
            {JSON.stringify(selectedStepInfo)}
            <DeltaDebuggingStepResultTable
              ddHunkInfo={ddResult.info}
              selectedStepInfo={selectedStepInfo}
            />
          </ProCard>
          <ProCard split={'horizontal'}>
            <DeltaDebuggingHunkBlocks ddHunkInfo={ddResult.info} />
          </ProCard>
        </ProCard>
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
