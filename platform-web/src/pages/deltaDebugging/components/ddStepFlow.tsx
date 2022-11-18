import { Steps, Collapse, Table } from 'antd';
import { ColumnsType } from 'antd/lib/table';
import { useState } from 'react';
import { ddResultItems } from '../data';

interface IProps {
  ddSteps: ddResultItems;
}

const DeltaDebuggingStepFlow: React.FC<IProps> = ({ ddSteps }) => {
  const [current, setCurrent] = useState<number>(0);

  const columnsHunkList = ddSteps.info.allHunks.map((data) => {
    return {
      title: data.hunkId,
      dataIndex: data.hunkId,
    };
  });
  columnsHunkList.splice(0, 0, {
    title: 'resultType',
    dataIndex: 'resultType',
  });

  const columns: ColumnsType<any> = columnsHunkList;

  const handleStepsChange = (value: number) => {
    // console.log('change', current);
    setCurrent(value);
  };
  return (
    <Steps initial={0} current={current} onChange={handleStepsChange} direction="vertical">
      {ddSteps.steps.map((resp) => {
        return (
          <Steps.Step
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
            subTitle={`Tested hunks: [${resp.testedHunks}]`}
            description={
              <Collapse
                onChange={(key) => {
                  console.log(key);
                }}
              >
                <Collapse.Panel
                  key={resp.stepNum}
                  header={'dd results'}
                  forceRender
                  style={{ overflow: 'auto' }}
                >
                  <Table
                    rowKey={`${resp.stepNum}-${resp.testResult}`}
                    columns={columns}
                    dataSource={resp.cProDDResults}
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

export default DeltaDebuggingStepFlow;
