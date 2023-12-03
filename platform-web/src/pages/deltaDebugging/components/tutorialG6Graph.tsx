import {drawSmellGraph} from '@/components/G6Graph/hunkRelationGraph';
// import { Spin } from 'antd';
import {useEffect} from 'react';
import {tutorialData} from './mockData';

const TutorialGraph: React.FC<{}> = () => {
  useEffect(() => {
    const data = tutorialData;
    drawSmellGraph(data, 'tutorial');
  }, []);
  return (
    <>
      <div id="tutorial" style={{width: 200, height: 50}}/>
    </>
  );
};

export default TutorialGraph;
