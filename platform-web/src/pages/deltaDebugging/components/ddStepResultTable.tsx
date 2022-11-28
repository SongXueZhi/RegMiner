import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Collapse, List, Skeleton } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import { useEffect, useState } from 'react';
import type { HunkEntityItems, DdStepsItems } from '../data';

interface IProps {
  allHunks: HunkEntityItems[];
  selectedStepInfo: DdStepsItems[];
}

function withSkeleton(element: JSX.Element | string | number | number | undefined) {
  return (
    element ?? <Skeleton title={{ width: '80px', style: { margin: 0 } }} paragraph={false} active />
  );
}

const DeltaDebuggingStepResultTable: React.FC<IProps> = ({ allHunks, selectedStepInfo }) => {
  const columnsHunkList: ProColumns<DdStepsItems>[] = [
    {
      title: 'Step',
      dataIndex: 'stepNum',
      width: 48,
    },
    {
      title: 'Result',
      dataIndex: 'stepTestResult',
      width: 48,
    },
    {
      title: 'cProb',
      dataIndex: 'cprob',
      render: (_, { cprob }) => {
        // const CProb = cprob.toString();
        // cprob.map((resp, index) => description.concat(`hunk ${index}: ${resp}`));
        // return withSkeleton(CProb);
        return cprob.map((num, index) => {
          return `Hunk ${index}: ${num.toFixed(5)} || `;
        });
      },
    },
    {
      title: 'dProb',
      dataIndex: 'dprob',
      hideInTable: true,
    },
  ];

  // const onChange = (key: string | string[]) => {
  // console.log(key);
  // };

  useEffect(() => {
    console.log(selectedStepInfo);
  }, [selectedStepInfo]);

  return (
    <>
      {/* {JSON.stringify(selectedStepInfo)} */}
      <Collapse>
        <Collapse.Panel header="Selected DD results" key={'selected-dd-results'}>
          {allHunks && selectedStepInfo ? (
            <ProTable
              rowKey="stepNum"
              bordered
              dataSource={selectedStepInfo}
              columns={columnsHunkList}
              pagination={false}
              search={false}
              toolBarRender={false}
            />
          ) : null}
        </Collapse.Panel>
      </Collapse>
    </>
  );
};

export default DeltaDebuggingStepResultTable;
