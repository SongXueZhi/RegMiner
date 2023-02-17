import { clearSmellGraph, drawSmellGraph } from '@/components/G6Graph/hunkRelationGraph';
import { drawRelationTreeGraph } from '@/components/G6Graph/hunkRelationTree';
import { drawAddNodeGraph } from '@/components/G6Graph/addNodeEdge';
// import { Spin } from 'antd';
import { useEffect } from 'react';
import { hunkRelationData, relationTreeData, addNodeEdgeData } from './mockData';

const DeltaDebuggingHunkRelationGraph: React.FC<{}> = () => {
  useEffect(() => {
    const data = hunkRelationData;
    const tree = relationTreeData;
    const add = addNodeEdgeData;
    // clearSmellGraph('dd-hunk-relation-graph');
    drawSmellGraph(data, 'dd-hunk-relation-graph');
    drawRelationTreeGraph(tree, 'tree');
    drawAddNodeGraph(add, 'add');
  }, []);
  return (
    <>
      <div id="dd-hunk-relation-graph" style={{ width: '100%', height: '33%' }} />;
      <div id="tree" style={{ width: '100%', height: '33%' }} />
      <div id="add" style={{ width: '100%', height: '33%' }} />
    </>
  );
};

export default DeltaDebuggingHunkRelationGraph;
