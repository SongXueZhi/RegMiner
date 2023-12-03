import React, {useEffect, useState} from 'react';
import {getDistanceDay} from '../utils';
import {SyncOutlined} from '@ant-design/icons';
import {Alert, Button, message, Progress, Steps, Tag} from 'antd';
import {getProcessInfo} from '../service';
import {ProgressInfoItems} from '../data';
import {useAccess} from 'umi';
import './index.less';

const {Step} = Steps;

// dashiboard
declare global {
  interface Window {
    isStoping?: boolean | false;
    timer: any;
  }
}
const progressContainer = {
  padding: '30px',
  background: '#fff',
  'margin-bottom': '20px',
  // marginBotton: '20px'
};

const ProjectProgress: React.FC<any> = () => {
  const access = useAccess();
  const [progressInfo, setProgressInfo] = useState<ProgressInfoItems>({
    currentProjectName: 'no data',
    projectQueueNum: 0,
    totalProjectNum: 0,
    totalStartTime: 0,
    projectStatTime: 0,
    totalProgress: 0,
    totalPRFCNum: 0,
    regressionNum: 0,
    prfcdoneNum: 0,
    currentRepoProgress: 0,
    finishedProject: 0,
  });
  const [distanceTime, setDistanceTime] = useState<string>('');
  const [repodistanceTime, setRepodistanceTime] = useState<string>('');
  //   const [isStoping, setIsStoping] = useState<boolean>(false);

  const updateProcessInfo = async (params?: any) => {
    window.isStoping = false;
    getProcessInfo().then((res: any) => {
      const newData: any = res.data;
      const distanceTime: any = getDistanceDay(Number(res.data.totalStartTime));
      const repodistanceTime: any = getDistanceDay(res.data.projectStatTime);
      window.currentProjectName = res.data.currentProjectName;
      const watingProject = Number(res.data.totalProjectNum) - Number(res.data.projectQueueNum) - 1;
      newData.watingProject = watingProject >= 0 ? watingProject : 0;
      newData.finishedProject = res.data.projectQueueNum;
      if (res.data.totalProjectNum) {
        newData.totalProgress = (
          (res.data.projectQueueNum / res.data.totalProjectNum) * 100 +
          (res.data.prfcdoneNum / res.data.totalPRFCNum / Number(res.data.totalProjectNum)) * 100
        ).toFixed(2);
      }
      if (res.data.totalPRFCNum) {
        newData.currentRepoProgress = (
          (res.data.prfcdoneNum / res.data.totalPRFCNum) *
          100
        ).toFixed(2);
      }
      setProgressInfo(newData);
      setDistanceTime(distanceTime);
      setRepodistanceTime(repodistanceTime);
    });
  };
  const resetProcessInfo = () => {
    if (access.allUsersFoo) {
      const data = {
        currentProjectName: 'no data',
        projectQueueNum: 0,
        totalProjectNum: 0,
        totalStartTime: 0,
        projectStatTime: 0,
        totalProgress: 0,
        totalPRFCNum: 0,
        regressionNum: 0,
        prfcdoneNum: 0,
        currentRepoProgress: 0,
        finishedProject: 0,
      };
      setProgressInfo(data);
      window.isStoping = true;
      window.clearInterval(window.timer);
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
  };

  useEffect(() => {
    updateProcessInfo();
    window.timer = setInterval(() => {
      //@ts-ignore
      const time = getDistanceDay(progressInfo.totalStartTime);
      //@ts-ignore
      const repotime = getDistanceDay(progressInfo.projectStatTime);
      setDistanceTime(time);
      setRepodistanceTime(repotime);
      updateProcessInfo();
      // distanceTime = getDistanceDay(progressInfo.totalStartTime)
    }, 60 * 1000);
  }, []);
  return (
    <>
      <div style={progressContainer}>
        <div className="header-container">
          {/* <Spin size="small" /> */}
          <h2 style={{marginBottom: '20px'}}>
            <div
              style={{
                display: 'inline-block',
                width: '5px',
                height: '12px',
                background: '#722ED1',
                marginRight: '10px',
              }}
            ></div>
            Processed Projects: {progressInfo.totalProjectNum} |{' '}
            <span>({progressInfo.totalProgress}%)</span>
            <div style={{position: 'absolute', right: '10px', top: '0px'}}>
              <Button
                type="primary"
                onClick={() => {
                  if (access.onlyAdminFoo) {
                    updateProcessInfo.bind(ProjectProgress);
                  } else {
                    message.error(
                      'Sorry, you have no right to do that. Please login or use another account!',
                    );
                  }
                }}
              >
                <span style={{color: '#fff'}}>Start</span>
              </Button>
              <Button
                style={{marginLeft: '10px'}}
                onClick={() => {
                  if (access.onlyAdminFoo) {
                    resetProcessInfo.bind(ProjectProgress);
                  } else {
                    message.error(
                      'Sorry, you have no right to do that. Please login or use another account!',
                    );
                  }
                }}
              >
                Stop
              </Button>
            </div>
          </h2>
        </div>
        <div style={{padding: '0 20px'}}>
          <Steps current={1} size="small">
            <Step
              title="Finished"
              description={`${progressInfo.finishedProject} project repositories are done.`}
            />
            <Step
              title="In Progress"
              icon={<SyncOutlined spin={!window.isStoping}/>}
              subTitle={distanceTime}
              description={`${window.currentProjectName} is processing`}
            />
            <Step
              title="Waiting"
              description={`${progressInfo.watingProject} project repositories are in queue`}
            />
          </Steps>
          {/* <Progress
              className="total-progress"
              percent={progressInfo.totalProgress}
              steps={progressInfo.totalProjectNum}
              size="default"
              showInfo={false}
              strokeWidth={12}
              strokeColor="#52c41a"
              trailColor="rgb(233 242 255)"
            /> */}
          <Progress
            className="myResProgress"
            strokeColor={{
              '0%': '#108ee9',
              '100%': '#87d068',
            }}
            trailColor="rgb(238 238 238)"
            percent={progressInfo.totalProgress}
            strokeWidth={10}
          />
        </div>
        <div style={{marginTop: '-10px'}} className="header-container">
          {/* <Spin size="small" /> */}
          <h2>
            <div
              style={{
                display: 'inline-block',
                width: '5px',
                height: '12px',
                background: '#722ED1',
                marginRight: '10px',
              }}
            ></div>
            {/* current project: {progressInfo.currentProjectName}  */}
            Processed Bug-fixing Commits |
            <span>
              {' '}
              {/* <SyncOutlined
                  style={{
                    fontSize: '20px',
                    marginTop: '-16px',
                    marginLeft: '10px',
                    marginRight: '4px',
                    color: '#722ED1',
                  }}
                  spin
                /> */}
              ({progressInfo.currentRepoProgress}%)
            </span>
            <h6 style={{marginLeft: '20px', color: '#666'}}>spend: {repodistanceTime}</h6>
          </h2>
        </div>
        {/* <Alert
            showIcon
            message="100 project repositories are in queue, 10 are done and fastjson is processing."
            type="info"
          /> */}
        <div style={{padding: '0 20px', display: 'flex', alignItems: 'center', height: '40px'}}>
          {/* <Spin size="small" style={{ marginTop: '-10px', marginRight: '10px' }} /> */}

          <Progress
            className="myResProgress"
            strokeColor={{
              '0%': '#108ee9',
              '100%': '#87d068',
            }}
            trailColor="rgb(238 238 238)"
            percent={progressInfo.currentRepoProgress}
            strokeWidth={12}
          />
        </div>
        {/* <Progress
          strokeColor={{
            from: '#108ee9',
            to: '#87d068',
          }}
          percent={99.9}
          status="active"
        /> */}
        <div style={{padding: '0 20px'}}>
          <div className="tips-container">
            <Alert
              type="info"
              showIcon
              className="dashboard-alert"
              message={
                <span className="content">
                  {window.currentProjectName}: Potential regression fixing commit:
                  <Tag className="tag-content" color="processing">
                    {progressInfo.totalPRFCNum}
                  </Tag>
                  Done:
                  <Tag className="tag-content" color="green">
                    {progressInfo.prfcdoneNum}
                  </Tag>
                  Bugs:
                  <Tag className="tag-content" color="#f50">
                    {progressInfo.regressionNum}
                  </Tag>
                  {/* <Button onClick={showDrawer}>show Regressions</Button> */}
                </span>
              }
            />
          </div>
          {/* <Row gutter={16}>
              <Col span={12}>
                <Card
                  title={`${progressInfo.regressionNum} regressions have been found`}
                  hoverable
                  className="progress-card"
                >
                  <Progress
                    type="circle"
                    strokeColor={{
                      '0%': '#108ee9',
                      '100%': '#87d068',
                    }}
                    percent={100}
                    strokeWidth={15}
                  />
                </Card>
              </Col>
              <Col span={8}>
                <Card title="-" hoverable className="progress-card">
                  <Tooltip title="3 done / 3 in progress / 4 to do">
                    <Progress percent={60} successPercent={30} type="circle" strokeWidth={15} />
                  </Tooltip>
                </Card>
              </Col>
              <Col span={12}>
                <Card title="something processing" hoverable className="progress-card">
                  <Progress
                    type="circle"
                    strokeColor={{
                      '0%': '#108ee9',
                      '100%': '#87d068',
                    }}
                    percent={90}
                    strokeWidth={15}
                  />
                </Card>
              </Col>
            </Row> */}
        </div>
      </div>
    </>
  );
};

export default ProjectProgress;
