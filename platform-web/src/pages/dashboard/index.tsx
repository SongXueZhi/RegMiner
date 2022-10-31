import { QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Divider, Skeleton, Tooltip, Drawer } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import TimeLine from './components/Timeline';

import { queryRegressionList, getDeatil } from './service';
import { Link } from 'react-router-dom';
import { stringify } from 'query-string';
import './index.less';
import ProjectProgress from './components/projectProgress';

declare global {
  interface Window {
    currentProjectName: any;
    currentBic: any;
    currentBfc: any;
  }
}

/**
 * 添加节点
 *
 * @param fields
 */
// const handleAdd = async (fields: API.RegressionItem) => {
//   const hide = message.loading('Adding');
//   try {
//     await addRegression({ ...fields });
//     hide();
//     message.success('Successfully added!');
//     return true;
//   } catch (error) {
//     hide();
//     message.error('Failed to add. Please try again!');
//     return false;
//   }
// };

function withSkeleton(element: JSX.Element | string | number | number | undefined) {
  return (
    element ?? <Skeleton title={{ width: '80px', style: { margin: 0 } }} paragraph={false} active />
  );
}

const DrawerbodyStyle = {
  // 'background-color': '#f5f5f5'
};

const DashboardPage: React.FC<{}> = () => {
  const [dashboardvisible, setVisible] = useState<boolean>(false);
  const [currentRegressionUuid, setCurRegressionUuid] = useState<string>('');
  const [currentBugid, setCurBugid] = useState<string>('');

  const actionRef = useRef<ActionType>();

  const showDrawer = () => {
    setVisible(true);
  };
  const onClose = () => {
    setVisible(false);
  };
  const [timeLineList, handleTimeLine] = useState<any>([]);
  const [idLists, handleIdLists] = useState<any>([]);
  let timeLineTotal: number = 0;
  const indicated: any = [];

  const timeLineDetail = async (
    bfc: any,
    rid: any,
    projectFullName: any,
    bugId: any,
    work: any,
  ) => {
    const res: any = await getDeatil({
      regressionUuid: bfc,
      projectName: projectFullName,
    });
    const arr: any = [];
    const indexList: number[] = [];
    const idList: any = [];
    const statusList: any = [];
    for (let i = 0; i < res.data.orderList?.length; i++) {
      indexList.push(Number(res.data.orderList[i][0]));
      idList.push(res.data.orderList[i][1]);
      statusList.push(res.data.orderList[i][2]);
    }
    const statusMap = {
      PASS: '#52c41a',
      FAL: 'RED',
      CE: '#ccc',
      UNKNOWN: '#ccc',
    };
    handleIdLists(indexList);
    for (let i = 0; i < Number(res.data.searchSpaceNum) - 1; i++) {
      if (indexList.indexOf(i) !== -1) {
        console.log(
          'item.index === ',
          work === idList[indexList.indexOf(i)],
          idList[indexList.indexOf(i)],
          work,
        );
        arr.push({
          index: work === idList[indexList.indexOf(i)] ? 'wc' : arr.length,
          name: work === idList[indexList.indexOf(i)] ? i + ':wc' : i,
          firstShow: indexList.indexOf(i),
          time: '',
          id: idList[indexList.indexOf(i)],
          status: statusList[indexList.indexOf(i)],
          color: statusMap[statusList[indexList.indexOf(i)]],
        });
      }
    }
    for (let i = 0; i < indexList.length; i++) {
      arr.forEach((item: any, index: any) => {
        if (item.name === indexList[i]) {
          indicated.push(index);
        }
      });
    }

    const sort: any = [];
    for (let i = 0; i < indicated.length; i++) {
      sort.push(indicated.indexOf(i));
    }
    const bfcName = Number(arr[arr.length - 1].name) + 1;
    console.log();
    arr.push({
      index: 'bfc',
      name: bfcName + ':bfc',
      firstShow: arr.length + 1,
      time: '',
      id: bfc,
      status: 'PASS',
      color: '#52c41a',
    });

    handleIdLists(indicated);
    handleTimeLine(arr);
    timeLineTotal = Number(res.searchSpaceNum);
    setCurRegressionUuid(rid);
    setCurBugid(bugId);
    // render()
  };

  const columns: ProColumns<API.RegressionItem>[] = [
    {
      title: 'No.',
      dataIndex: 'index',
      width: 48,
      render: (_, record) => {
        return record.index + 1;
      },
      search: false,
    },
    {
      title: 'bug id',
      dataIndex: 'regressionUuid',
      search: false,
      width: 200,
      render: (_, { projectFullName, regressionUuid, index }) => {
        return withSkeleton(
          regressionUuid ? (
            index <= 49 ? (
              <Link
                to={{
                  pathname: '/editor',
                  search: stringify({ regressionUuid }),
                }}
              >
                {projectFullName?.split('/')[1]}_{index}
              </Link>
            ) : (
              regressionUuid
            )
          ) : (
            '暂无数据'
          ),
        );
      },
    },
    {
      title: 'actions',
      hideInForm: true,
      // hideInTable: true,
      search: false,
      // fixed: 'right',
      render: (_, { bfc, regressionUuid, projectFullName, bic, bugId, work, index }) => [
        <Divider type="vertical" />,
        <Button
          danger
          onClick={() => {
            // handleRemove(regressionUuid).then(() => {
            console.log('regressionUuid,bic:', bic, bfc, regressionUuid, projectFullName, bugId);
            window.currentBic = bic;
            // });
            const bug = `${projectFullName}_${index}`;

            timeLineDetail(bfc, regressionUuid, projectFullName, bug, work);
            onClose();
          }}
        >
          Time line
        </Button>,
      ],
    },
  ];
  // const CodeEditorRef = React.createRef;
  const ProgressInfoRef = React.createRef;
  // const reset = () => {
  //   //@ts-ignore
  //   ProgressInfoRef.resetProcessInfo();
  // };

  return (
    <PageContainer
      header={{
        style: { width: '100%' },
        title: 'Dashboard',
        subTitle: (
          <div className="sub-title-container">
            <div>process dashboard</div>
            <div>
              <Button className="sub-title-header" type="primary" onClick={showDrawer}>
                Show Finished Regressions
              </Button>
              {/* <Button type="primary" style={{ marginLeft: '10px' }}>
                Start
              </Button>
              <Button style={{ marginLeft: '10px' }} onClick={reset}>
                Stop
              </Button> */}
            </div>
          </div>
        ),
      }}
    >
      <ProjectProgress ref={ProgressInfoRef} />
      {/* <CodeEditor ref={CodeEditorRef} /> */}
      <div className="regressionTimeline">
        <h2>
          <div
            style={{
              display: 'inline-block',
              width: '5px',
              height: '12px',
              background: '#722ED1',
              marginRight: '10px',
            }}
          />
          Time Line{' '}
          <Tooltip title="choose a regression to check timeline" key={1}>
            {' '}
            <QuestionCircleOutlined />
          </Tooltip>
        </h2>
        <div> Current Bug Id: {currentBugid}</div>
        <div className="timeline-container">
          <TimeLine
            lineList={timeLineList}
            total={timeLineTotal}
            indicated={idLists}
            currentRegressionUuid={currentRegressionUuid}
            cur={0}
          />
        </div>
      </div>
      <Drawer
        bodyStyle={DrawerbodyStyle}
        title="Finished Regressions List"
        placement={'right'}
        closable={false}
        onClose={onClose}
        visible={dashboardvisible}
        key={'right'}
        width={450}
      >
        <ProTable<API.RegressionItem>
          headerTitle="Regression List"
          actionRef={actionRef}
          rowKey="regressionUuid"
          // @ts-ignore
          request={(params) =>
            queryRegressionList({
              regression_uuid: params.regressionUuid,
              keyword: params.keyword,
            })
          }
          columns={columns}
          search={false}
          // pagination={{
          //   pageSize: 20,
          //   pageSizeOptions: undefined,
          // }}
        />
      </Drawer>
      {/* dashboard */}
    </PageContainer>
  );
};

export default DashboardPage;
