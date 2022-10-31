import { clearSmellGraph, drawSmellGraph } from '@/components/G6Graph/hunkRelationGraph';
import { Spin } from 'antd';
import { useEffect } from 'react';
import { hunkRelationData } from './mockData';

const DeltaDebuggingHunkRelationGraph: React.FC<{}> = () => {
  useEffect(() => {
    const data = hunkRelationData;
    clearSmellGraph('dd-hunk-relation-graph');
    drawSmellGraph(data, 'dd-hunk-relation-graph');
  }, []);
  return (
    <>
      <div id="dd-hunk-relation-graph" style={{ width: '80%', height: 250 }}>
        <Spin />
      </div>
      ;
    </>
  );
};

export default DeltaDebuggingHunkRelationGraph;
