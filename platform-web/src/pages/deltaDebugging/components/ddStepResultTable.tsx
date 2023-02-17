import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Col, Collapse, Row, Typography } from 'antd';
import { useEffect, useMemo, useState } from 'react';
import type { HunkEntityItems, DdStepsItems } from '../data';

interface IProps {
  allHunks: HunkEntityItems[];
  allStepInfo?: DdStepsItems[];
  selectedHunk?: DdStepsItems;
}

// function withSkeleton(element: JSX.Element | string | number | number | undefined) {
//   return (
//     element ?? <Skeleton title={{ width: '80px', style: { margin: 0 } }} paragraph={false} active />
//   );
// }

const DeltaDebuggingStepResultTable: React.FC<IProps> = ({ allHunks, selectedHunk }) => {
  const [dataSource, setDataSource] = useState<DdStepsItems[]>([]);

  const columns: ProColumns<DdStepsItems>[] = useMemo(
    () => [
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
        render: (_, { stepTestedInx, cprob }) => {
          return (
            <Row justify="center" wrap={true}>
              {cprob.map((num, index) => {
                return (
                  <Col span={4}>
                    <Typography.Text mark={stepTestedInx ? stepTestedInx.includes(index) : false}>
                      Hunk {index}:
                    </Typography.Text>
                    <Typography.Text keyboard>{num.toFixed(3)}</Typography.Text>
                  </Col>
                );
              })}
            </Row>
          );
        },
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [],
  );

  useEffect(() => {
    if (selectedHunk) {
      // every step can only loaded once
      if (!dataSource.some((d) => d.stepNum === selectedHunk.stepNum)) {
        const list = dataSource.concat([selectedHunk]);
        setDataSource(list);
      }
    } else {
      setDataSource([]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedHunk]);

  return (
    <Collapse>
      <Collapse.Panel header={'Selected DD results'} key="selected-dd-results">
        {allHunks ? (
          <ProTable
            key={'selected-step-info-table'}
            rowKey="stepNum"
            bordered
            dataSource={dataSource}
            // request={}
            columns={columns}
            pagination={false}
            search={false}
            toolBarRender={false}
          />
        ) : null}
      </Collapse.Panel>
      {/* <Collapse.Panel header="test" key={'test'}>
          <ProTable
            rowKey="stepNum"
            bordered
            columns={columnsHunkList}
            pagination={false}
            search={false}
            toolBarRender={false}
            dataSource={ddResult.stepInfo}
          />
        </Collapse.Panel> */}
    </Collapse>
  );
};

export default DeltaDebuggingStepResultTable;
