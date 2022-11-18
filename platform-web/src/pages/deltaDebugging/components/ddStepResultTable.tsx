import { Collapse, Table } from 'antd';
import { ColumnsType } from 'antd/lib/table';
import { useEffect, useState } from 'react';
import { ddInfoItems, ddStepsItems } from '../data';

interface IProps {
  ddHunkInfo: ddInfoItems;
  selectedStepInfo: ddStepsItems[];
}

const DeltaDebuggingStepResultTable: React.FC<IProps> = ({ ddHunkInfo, selectedStepInfo }) => {
  const [tableColumns, setTableColumns] = useState<ColumnsType<any>>([]);
  const [tableDataSource, setTableDataSource] = useState<any>([]);

  useEffect(() => {
    const columnsHunkList = [
      {
        title: 'Result type',
        dataIndex: 'resultType',
      },
      {
        title: 'Step',
        dataIndex: 'stepNum',
      },
    ];
    ddHunkInfo.allHunks.map((data) => {
      columnsHunkList.push({
        title: data.hunkId,
        dataIndex: data.hunkId,
      });
    });
    setTableColumns(columnsHunkList);

    const dataSource = selectedStepInfo.map((data) => {
      const result = data.cProDDResults
      return result;
    });
    setTableDataSource(dataSource);
  }, [ddHunkInfo, selectedStepInfo]);

  const onChange = (key: string | string[]) => {
    console.log(key);
  };
  return (
    // <Collapse
    //   onChange={(key) => {
    //     console.log(key);
    //   }}
    // >
    //   {selectedStepInfo.map((resp) => {
    //     <Collapse.Panel
    //       key={resp.stepNum}
    //       header={'dd results'}
    //       // forceRender
    //       // style={{ overflow: 'auto' }}
    //     >
    //       <Table
    //         rowKey={`${resp.stepNum}-${resp.testResult}`}
    //         columns={columns}
    //         dataSource={resp.testResultData}
    //         pagination={false}
    //       />
    //     </Collapse.Panel>;
    //   })}
    // </Collapse>
    <Collapse onChange={onChange}>
      <Collapse.Panel header="Selected DD results" key={'selected-dd-results'}>
        <Table
          // rowKey={`${resp.stepNum}-${resp.testResult}`}
          bordered
          dataSource={tableDataSource}
          columns={tableColumns}
          pagination={false}
        />
      </Collapse.Panel>
    </Collapse>
  );
};

export default DeltaDebuggingStepResultTable;
