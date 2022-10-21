import { Steps, Collapse, Table } from 'antd';
import { ColumnsType } from 'antd/lib/table';
import { useState } from 'react';
import { ddResult } from './mockData';

interface IProps {
  ddSteps: string[];
}

const ddStepFlow: React.FC<IProps> = ({ ddSteps }) => {
  const [current, setCurrent] = useState(0);
  const columns: ColumnsType<any> = [];

  const columnsHunkList = ddResult.info.allHunks.map((data) => {
    return {
      title: data.hunkId,
      dataIndex: data.hunkId,
    };
  });

  const handleStepsChange = (value: number) => {
    console.log('change', current);
    setCurrent(value);
  };
  return (
    <Steps initial={0} current={current} onChange={handleStepsChange} direction="vertical">
      {ddResult.steps.map((resp) => {
        return (
          <Steps.Step
            title={`Step result: ${resp.stepResult}`}
            subTitle={`Tested hunks: [${resp.testedHunks}]`}
            description={
              <Collapse
                onChange={(key) => {
                  console.log(key);
                }}
              >
                <Collapse.Panel key={resp.stepNum} header={'dd results'}>
                  <Table
                    rowKey={`${resp.stepNum}-${resp.stepResult}`}
                    columns={columns}
                    dataSource={resp.testResults}
                    pagination={false}
                  />
                </Collapse.Panel>
              </Collapse>
            }
          ></Steps.Step>
        );
      })}
    </Steps>
  );
};

export default ddStepFlow;
