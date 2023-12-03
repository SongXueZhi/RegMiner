import {drawAddNodeGraph} from '@/components/G6Graph/addNodeEdge';
// import { Spin } from 'antd';
import {useEffect} from 'react';
import {addNodeEdgeData, addNodeEdgeDataFake, hunkRelationData, relationTreeData} from './mockData';

const DeltaDebuggingHunkRelationGraph: React.FC<{}> = () => {
  useEffect(() => {
    const data = hunkRelationData;
    const tree = relationTreeData;
    const add = addNodeEdgeData;
    const fake = addNodeEdgeDataFake;
    // clearSmellGraph('dd-hunk-relation-graph');
    // drawSmellGraph(data, 'dd-hunk-relation-graph');
    drawAddNodeGraph(fake, 'add');
  }, []);
  return (
    <>
      {/* <div id="dd-hunk-relation-graph" style={{ width: '100%', height: '33%' }} />;
      <div id="tree" style={{ width: '100%', height: '33%' }} /> */}
      <div id="add" style={{width: '100%', height: '100%'}}/>
    </>
  );
};

export default DeltaDebuggingHunkRelationGraph;
