import { PlayCircleOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import ProCard from '@ant-design/pro-card';
import { PageContainer } from '@ant-design/pro-layout';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  Button,
  Card,
  Descriptions,
  Drawer,
  InputNumber,
  message,
  Popover,
  Skeleton,
  Spin,
  Steps,
  Tooltip,
  Typography,
} from 'antd';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import type { IRouteComponentProps } from 'umi';
import { queryRegressionDetail } from '../editor/service';
import { queryRegressionList } from '../regression/service';
import DeltaDebuggingHunkBlocks from './components/ddHunkBlocks';
import DeltaDebuggingHunkRelationGraph from './components/ddHunkRelationGraph';
import DeltaDebuggingStepResultTable from './components/ddStepResultTable';
import TutorialGraph from './components/tutorialG6Graph';
import type { DdStepsItems, HunkEntityItems } from './data';
import { runDeltaDebugging, runDeltaDebuggingByStep } from './service';

function withSkeleton(element: JSX.Element | string | number | number | undefined) {
  return (
    element ?? <Skeleton title={{ width: '80px', style: { margin: 0 } }} paragraph={false} active />
  );
}

const InteractiveDeltaDebuggingPage: React.FC<IRouteComponentProps> = () => {
  const [sidebarRegressionMenu, setSidebarRegressionMenu] = useState<boolean>(true);
  const [running, setRunning] = useState<boolean>(false);
  const [currRegressionUuid, setCurrRegressionUuid] = useState<string>();
  const [currProjectName, setCurrProjectName] = useState<string>();
  const [currRevisionName, setCurrRevisionName] = useState<string>();
  const [testCaseName, setTestCaseName] = useState<string>();
  const [BIC, setBIC] = useState<string>();
  const [BICURL, setBICURL] = useState<string>();
  const [BFC, setBFC] = useState<string>();
  const [BFCURL, setBFCURL] = useState<string>();
  const [regressionDescription, setRegressionDescription] = useState<string>();
  // const [ddResult, setDdResult] = useState<ddResultItems>();
  const [allHunks, setAllHunks] = useState<HunkEntityItems[]>([]);
  const [allStepInfo, setAllStepInfo] = useState<DdStepsItems[]>([]);
  const [selectedStepInfo, setSelectedStepInfo] = useState<DdStepsItems>();
  const [selectedStepTestedHunksIndex, setSelectedStepTestedHunksIndex] = useState<number[]>([]);
  const [runTimes, setRunTimes] = useState<number>(1);
  const [startStepNum, setStartStepNum] = useState<number>(0);
  const [endStepNum, setEndStepNum] = useState<number>(0);
  const actionRef = useRef<ActionType>();

  const RegressionColumns: ProColumns<API.RegressionItem>[] = [
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
      title: 'Bug ID',
      dataIndex: 'regressionUuid',
      search: false,
      width: 200,
      render: (_, { projectFullName, regressionUuid, index }) => {
        return withSkeleton(
          regressionUuid ? `${projectFullName?.split('/')[1]}_${index}` : 'No Data',
        );
      },
    },
    {
      title: 'Revision',
      search: false,
      render: (_, { projectFullName, regressionUuid, index }) => {
        return withSkeleton(
          regressionUuid ? (
            <>
              <Button
                type="link"
                onClick={() => {
                  setCurrRegressionUuid(regressionUuid);
                  setCurrProjectName(projectFullName?.split('/')[1] + '_' + index);
                  setCurrRevisionName('bic');
                  setSidebarRegressionMenu(false);
                }}
              >
                BIC
              </Button>
              <Button
                type="link"
                disabled
                onClick={() => {
                  setCurrRegressionUuid(regressionUuid);
                  setCurrProjectName(projectFullName?.split('/')[1] + '_' + index);
                  setCurrRevisionName('bfc');
                  setSidebarRegressionMenu(false);
                }}
              >
                BFC
              </Button>
            </>
          ) : (
            'No Data'
          ),
        );
      },
    },
    // {
    //   title: 'actions',
    //   hideInForm: true,
    //   // hideInTable: true,
    //   search: false,
    //   // fixed: 'right',
    //   render: (_, { bfc, regressionUuid, projectFullName, bic, bugId, work, index }) => [
    //     <Divider type="vertical" />,
    //     <Button
    //       danger
    //       onClick={() => {
    //         // handleRemove(regressionUuid).then(() => {
    //         // console.log('regressionUuid,bic:', bic, bfc, regressionUuid, projectFullName, bugId);
    //         window.currentBic = bic;
    //         // });
    //         const bug = `${projectFullName}_${index}`;
    //         timeLineDetail(bfc, regressionUuid, projectFullName, bug, work);
    //         onClose();
    //       }}
    //     >
    //       Time line
    //     </Button>,
    //   ],
    // },
  ];

  const onReload = useCallback(() => {
    setAllHunks([]);
    setAllStepInfo([]);
    setSelectedStepInfo(undefined);
  }, []);

  const handleRunDD = async () => {
    if (currRegressionUuid && currRevisionName) {
      setRunning(true);
      await runDeltaDebugging({
        regression_uuid: currRegressionUuid,
        revision_name: currRevisionName,
        start_step: 0,
        userToken: '123',
      }).then((data) => {
        if (data !== null) {
          setAllHunks(data.allHunkEntities);
          setAllStepInfo(data.stepInfo);
        }
        setRunning(false);
      });
    }
  };
  const handleRunDDByStep = () => {
    const targetStep = allStepInfo.find((d) => d.stepNum === startStepNum);
    if (currRegressionUuid && currRevisionName && targetStep) {
      setRunning(true);
      onReload();
      runDeltaDebuggingByStep({
        regressionUuid: currRegressionUuid,
        revisionName: currRevisionName,
        startStep: startStepNum,
        endStep: endStepNum,
        cprob: targetStep.cprob,
        leftIdx2Test: targetStep.leftIdx2Test,
        testedHunkIdx: targetStep.stepTestedInx ? targetStep.stepTestedInx : [],
        userToken: '123',
      }).then((data) => {
        if (data !== null) {
          setAllHunks(data.allHunkEntities);
          setAllStepInfo(data.stepInfo);
        }
        setRunning(false);
      });
    } else {
      message.error('Something went wrong! try again');
    }
  };

  const handleStepClick = (stepData: DdStepsItems) => {
    // update dd result table
    setSelectedStepInfo(stepData);
    // update dd hunk blocks
    setSelectedStepTestedHunksIndex(stepData.stepTestedInx !== null ? stepData.stepTestedInx : []);
  };

  const handleOnSelectedHunks = useCallback((selectedHunksIdx: number[]) => {
    setSelectedStepTestedHunksIndex(selectedHunksIdx);
  }, []);

  useEffect(() => {
    onReload();

    if (currRegressionUuid) {
      // regressionCheckout({ regression_uuid: currRegressionUuid, userToken: '123' }).then(() => {
      queryRegressionDetail({
        regression_uuid: currRegressionUuid,
        userToken: '123',
      }).then((data) => {
        if (data !== null && data !== undefined) {
          // setListBFC(data.bfcChangedFiles);
          // setListBIC(data.bicChangedFiles);
          setBFC(data.bfc);
          setBIC(data.bic);
          setBFCURL(data.bfcURL);
          setBICURL(data.bicURL);
          // setProjectFullName(data.projectFullName);
          setTestCaseName(data.testCaseName);
          // setTestFilePath(data.testFilePath);
          setRegressionDescription(data.descriptionTxt);
        }
        // setIsLoading(false);
      });
      // });
    }
  }, [currRegressionUuid, onReload]);

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
                Show Bugs
              </Button>
            </div>
          </div>
        ),
        footer: (
          <div style={{ display: 'inline-flex', alignItems: 'center' }}>
            <Descriptions column={3} style={{ flex: 1 }}>
              <Descriptions.Item label={'Project'} labelStyle={{ fontWeight: 'bold' }}>
                <Typography.Text strong>{currProjectName}</Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item label={'Revision'} labelStyle={{ fontWeight: 'bold' }}>
                {currRevisionName ? currRevisionName.toUpperCase() : null}
              </Descriptions.Item>
              {currRevisionName !== 'bfc' ? (
                <Descriptions.Item
                  label={'Bug Inducing Commit'}
                  labelStyle={{ fontWeight: 'bold' }}
                >
                  <Typography.Link keyboard href={BICURL} target="_blank">
                    {BIC?.slice(0, 8)}...
                  </Typography.Link>
                  <br />
                </Descriptions.Item>
              ) : (
                <Descriptions.Item label={'Bug Fixing Commit'} labelStyle={{ fontWeight: 'bold' }}>
                  <Typography.Link keyboard href={BFCURL} target="_blank">
                    {BFC?.slice(0, 8)}...
                  </Typography.Link>
                  <br />
                </Descriptions.Item>
              )}
              <Descriptions.Item label={'Regression UUID'} labelStyle={{ fontWeight: 'bold' }}>
                <Typography.Text>{currRegressionUuid}</Typography.Text>
              </Descriptions.Item>

              <Descriptions.Item
                label={'Regression description'}
                labelStyle={{ fontWeight: 'bold' }}
              >
                <Typography.Text>{regressionDescription}</Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item label={'Regression testcase'} labelStyle={{ fontWeight: 'bold' }}>
                <Typography.Text>{testCaseName}</Typography.Text>
              </Descriptions.Item>
            </Descriptions>
          </div>
        ),
      }}
    >
      <div style={{ display: 'flex', marginBottom: 10 }}>
        <ProCard
          title={
            <>
              <Button
                icon={<PlayCircleOutlined />}
                onClick={handleRunDD}
                size="middle"
                shape="round"
                style={{ marginBottom: '10px' }}
                type="primary"
              >
                Delta Debug
              </Button>
            </>
          }
          headStyle={{ height: 60 }}
          bodyStyle={{ height: 700 }}
          bordered
          style={{ width: '25%', overflow: 'auto' }}
        >
          <Steps direction="vertical">
            {allStepInfo
              ? allStepInfo.map((resp) => {
                  return (
                    <Steps.Step
                      onClick={() => handleStepClick(resp)}
                      key={'step_' + resp.stepNum}
                      status={
                        resp.stepTestResult === 'FAIL'
                          ? 'error'
                          : resp.stepTestResult === 'CE'
                          ? 'process'
                          : resp.stepTestResult === 'PASS'
                          ? 'finish'
                          : 'process'
                      }
                      title={`Step ${resp.stepNum} result: ${resp.stepTestResult}`}
                      description={`Tested hunks: [ ${
                        resp.stepTestedInx === null ? [] : resp.stepTestedInx
                      } ]`}
                    />
                  );
                })
              : null}
          </Steps>
          <Spin size="large" spinning={running} />
        </ProCard>
        <ProCard
          title={
            <>
              {selectedStepInfo ? (
                <>
                  <Typography.Text style={{ fontSize: 20 }}>
                    Step {selectedStepInfo?.stepNum} Details{' '}
                  </Typography.Text>
                  <Button
                    icon={<PlayCircleOutlined />}
                    onClick={handleRunDDByStep}
                    size="middle"
                    shape="round"
                    style={{ marginLeft: '5px', marginRight: '5px' }}
                    type="primary"
                  >
                    Delta Debug by steps
                  </Button>
                  <InputNumber
                    min={0}
                    max={allStepInfo.length}
                    value={runTimes}
                    defaultValue={1}
                    onChange={(value) => {
                      if (value !== null) {
                        setRunTimes(value);
                        // run the modified step needs the previous step info
                        setStartStepNum(selectedStepInfo.stepNum - 1);
                        setEndStepNum(selectedStepInfo.stepNum + value - 1);
                      } else {
                        message.error('Unexpected run times, please enter a integer number.');
                      }
                    }}
                    style={{ width: 60 }}
                    controls={false}
                  />
                  <Tooltip title={'Number of Runs'}>
                    <QuestionCircleOutlined style={{ marginLeft: 5 }} />
                  </Tooltip>
                </>
              ) : (
                <Typography.Title level={4}>Selected Delta Debug Step</Typography.Title>
              )}
            </>
          }
          headStyle={{ height: 60 }}
          bodyStyle={{ height: 700 }}
          bordered
          style={{ width: '75%', overflow: 'auto' }}
          split={'horizontal'}
        >
          <ProCard split="horizontal" key={'DD-result-table'}>
            <DeltaDebuggingStepResultTable
              allHunks={allHunks}
              allStepInfo={allStepInfo}
              selectedHunk={selectedStepInfo}
            />
          </ProCard>
          <ProCard key={'DD-hunk-blocks'}>
            <DeltaDebuggingHunkBlocks
              regressionUuid={currRegressionUuid}
              revision={currRevisionName}
              allHunkInfo={allHunks}
              onSelectedHunks={handleOnSelectedHunks}
            />
          </ProCard>
        </ProCard>
      </div>
      <div>
        <Card
          title={
            <div style={{ display: 'flex' }}>
              <Typography.Title level={4}>Hunk Relation Graph</Typography.Title>
              <Popover style={{ width: 500 }} content={<TutorialGraph />} trigger="hover">
                <QuestionCircleOutlined style={{ fontSize: 18 }} />
              </Popover>
            </div>
          }
          headStyle={{ height: 85 }}
          bodyStyle={{ height: 700 }}
          bordered
          style={{ width: '100%', overflow: 'auto' }}
        >
          <DeltaDebuggingHunkRelationGraph />
        </Card>
      </div>
      <Drawer
        // bodyStyle={DrawerbodyStyle}
        title="Regression List"
        placement={'right'}
        onClose={() => setSidebarRegressionMenu(false)}
        open={sidebarRegressionMenu}
        key={'right'}
        width={450}
      >
        <ProTable<API.RegressionItem>
          actionRef={actionRef}
          rowKey="regressionUuid"
          request={(params) =>
            queryRegressionList({
              regression_uuid: params.regression_uuid,
              keyword: params.keyword,
              page: params.current ?? 1,
              ps: params.pageSize ?? 10,
            })
          }
          columns={RegressionColumns}
          search={false}
        />
      </Drawer>
    </PageContainer>
  );
};

export default InteractiveDeltaDebuggingPage;
