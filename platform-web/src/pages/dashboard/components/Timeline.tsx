import React, {useEffect, useState} from 'react';
import {Button, Col, Row} from 'antd';
import {Link} from 'react-router-dom';
import {stringify} from 'querystring';
import './index.less';

export type TimeLineProps = {
  lineList?: any;
  total?: number;
  indicated: number[];
  currentRegressionUuid: string;
  cur: number;
};

const TimeLine: React.FC<TimeLineProps> = (props) => {
  const {currentRegressionUuid, lineList, indicated, cur} = props;
  const [currentStep, setCurrentStep] = useState<number>(cur);
  const [currentPoint, setCurrentPoint] = useState<any>(0);
  const [arr, setArr] = useState<any>([]);
  const [tailWidth, setTailWidth] = useState<any>(800);
  const reset = () => {
    setCurrentStep(0);
    setArr([]);
  };
  useEffect(() => {
    reset();
    setTailWidth(Number(lineList.length) * 80);
  }, [currentRegressionUuid]);

  // const nowIndex = indicated[currentStep] || 0
  // const [indicated, setIndicated] = useState<any>([0, 1, 2, 3, 4, 5, 8, 9, 15, 14, 13, 12, 11, 10, 7, 6]);
  const forward = () => {
    if (currentStep >= indicated.length - 1) {
      setCurrentStep(0);
      setArr([]);
    } else {
      const narr = arr;
      setArr(narr);
      // if(lineList[indicated[currentStep]]){
      narr.push(lineList[indicated[currentStep]].name);

      // }
      // console.log('lineList', lineList, indicated);
      setCurrentPoint(lineList[indicated[currentStep + 1]].id);
      setCurrentStep(currentStep + 1);
    }
    const dom = document.getElementsByClassName('stage-loc')[0];
    if (dom) {
      dom.scrollIntoView({
        behavior: 'smooth',
      });
    }
  };
  const pre = () => {
    if (currentStep > 0) {
      setCurrentPoint(lineList[indicated[currentStep - 1]].index);
      setCurrentStep(currentStep - 1);
      const narr = arr;
      narr.pop();
      setArr(narr);
    }
    const dom = document.getElementsByClassName('stage-loc')[0];
    if (dom) {
      dom.scrollIntoView({
        behavior: 'smooth',
      });
    }
  };

  const listName = lineList.map((item: any, key: any) => {
    return (
      <Link
        to={{
          pathname: '/editor',
          search: stringify({
            regressionUuid: currentRegressionUuid,
            bic: item.id,
          }),
        }}
      >
        <Col className="col-container">
          <div className="col-container">
            <span className="name">
              {item.index === 'wc' || item.index === 'bfc' || window.currentBic === item.id ? (
                <span style={{color: 'rgb(255 89 29)', fontWeight: 600}}>{item.name}</span>
              ) : (
                <span>{item.name}</span>
              )}
              {window.currentBic === item.id ? (
                <span style={{color: 'rgb(255 89 29)', fontWeight: 600}}>:bic</span>
              ) : (
                ''
              )}{' '}
              <span style={{fontSize: '10px'}}>({item.status})</span> ({item.firstShow})
            </span>
            {arr.indexOf(item.name) === -1 ? (
              <div className="u-dot" style={{background: item.color}}></div>
            ) : (
              <div className="u-dot u-passed-dot" style={{background: item.color}}></div>
            )}
            {/* <div className="u-dot"></div> */}

            {indicated[currentStep] === item.index ? (
              <div>
                <div className="u-dot current"></div>
                <img height="16px" src="./projectStageLoc.svg" className="stage-loc"/>
              </div>
            ) : (
              <div></div>
            )}
          </div>
        </Col>{' '}
      </Link>
    );
  });

  return (
    <div>
      <div className="op-list">
        <Button onClick={forward}>Next</Button>
        <Button onClick={pre}>Previous</Button>step:{currentStep} | commit index:{' '}
        {lineList && lineList.length ? (
          <span>
            {lineList[indicated[currentStep]]?.name} | id :{' '}
            <Link
              to={{
                pathname: '/detail',
                search: stringify({
                  regressionUuid: currentRegressionUuid,
                  bic: lineList[indicated[currentStep]]?.id,
                }),
              }}
            >
              {lineList[indicated[currentStep]]?.id}
            </Link>
          </span>
        ) : null}
      </div>
      <div className="container">
        <div className="u-tail" style={{width: `${tailWidth}px`}}></div>
        <Row
          justify="space-around"
          align="middle"
          className="row-container"
          style={{width: `${tailWidth}px`}}
        >
          {listName}
        </Row>
      </div>
    </div>
  );
};
export default TimeLine;
