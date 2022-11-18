import { ddResultItems } from '../data';

// case 6
export const ddResult: ddResultItems = {
  info: {
    projectFullName: 'this is Project Name',
    regressionUuid: '123-456',
    revision: 'bfc',
    filePath: '/dome/test',
    allHunks: [
      {
        hunkId: 'hunkId_1',
        oldCode: 'this is old code 1',
        newCode: 'this is new Code 1',
      },
      {
        hunkId: 'hunkId_2',
        oldCode: 'this is old code 2',
        newCode: 'this is new Code 2',
      },
      {
        hunkId: 'hunkId_3',
        oldCode: 'this is old code 3',
        newCode: 'this is new Code 3',
      },
      {
        hunkId: 'hunkId_4',
        oldCode: 'this is old code 4',
        newCode: 'this is new Code 4',
      },
      {
        hunkId: 'hunkId_5',
        oldCode: 'this is old code 5',
        newCode: 'this is new Code 5',
      },
      {
        hunkId: 'hunkId_6',
        oldCode: 'this is old code 6',
        newCode: 'this is new Code 6',
      },
    ],
  },
  steps: [
    {
      stepNum: 1,
      testResult: 'failed',
      testedHunks: [],
      cProDDResults: [0.213, 0.213, 0.213, 0.213, 0.213, 0.213],
    },
    {
      stepNum: 2,
      testResult: 'failed',
      testedHunks: ['hunkId_1', 'hunkId_4'],
      cProDDResults: [0.213, 0.345, 0.345, 0.213, 0.345, 0.345],
    },
    {
      stepNum: 3,
      testResult: 'CE',
      testedHunks: ['hunkId_2', 'hunkId_4', 'hunkId_5', 'hunkId_6'],
      cProDDResults: [0.213, 0.345, 0.345, 0.213, 0.345, 0.345],
    },
    {
      stepNum: 4,
      testResult: 'CE',
      testedHunks: ['hunkId_2', 'hunkId_3', 'hunkId_4', '    hunkId_5'],
      cProDDResults: [0.213, 0.345, 0.345, 0.213, 0.345, 0.345],
    },
    {
      stepNum: 5,
      testResult: 'CE',
      testedHunks: ['hunkId_1', 'hunkId_2', 'hunkId_3', ' hunkId_6'],
      cProDDResults: [0.213, 0.345, 0.345, 0.213, 0.345, 0.345],
    },
    {
      stepNum: 6,
      testResult: 'pass',
      testedHunks: ['hunkId_1', 'hunkId_2', 'hunkId_3', ' hunkId_4'],
      cProDDResults: [0.213, 0.345, 0.345, 0.213, 0.0, 0.0],
    },
    {
      stepNum: 7,
      testResult: 'CE',
      testedHunks: ['hunkId_2', 'hunkId_3'],
      cProDDResults: [0.213, 0.345, 0.345, 0.213, 0.0, 0.0],
    },
    {
      stepNum: 8,
      testResult: 'failed',
      testedHunks: ['hunkId_3', ' hunkId_4'],
      cProDDResults: [0.439, 0.712, 0.345, 0.213, 0.0, 0.0],
    },
    {
      stepNum: 9,
      testResult: 'failed',
      testedHunks: ['hunkId_1', 'hunkId_2'],
      cProDDResults: [0.439, 0.712, 0.712, 0.439, 0.0, 0.0],
    },
    {
      stepNum: 10,
      testResult: 'CE',
      testedHunks: ['hunkId_2', 'hunkId_3'],
      cProDDResults: [0.439, 0.712, 0.712, 0.439, 0.0, 0.0],
    },
  ],
};

export const hunkRelationData = {
  nodes: [
    {
      path: '/src/main/java/redis/clients/jedis/Pipeline.java',
      size: 30,
      name: 'Pipeline.java',
      id: '1',
      label: 1,
    },
    {
      path: '/src/main/java/redis/clients/jedis/Jedis.java',
      size: 30,
      name: 'Jedis.java',
      id: '2',
      label: 2,
    },
    {
      path: '/src/main/java/redis/clients/jedis/Transaction.java',
      size: 30,
      name: 'Transaction.java',
      id: '3',
      label: 3,
    },
  ],
  smells: [
    {
      nodes: [
        {
          path: '/src/main/java/redis/clients/jedis/Pipeline.java',
          index: 1,
        },
        {
          path: '/src/main/java/redis/clients/jedis/Jedis.java',
          index: 2,
        },
        {
          path: '/src/main/java/redis/clients/jedis/Transaction.java',
          index: 3,
        },
      ],
      name: '编译率高的开源项目-jedis-master-0',
    },
  ],
  coreNode: '0',
  edges: [
    {
      dependsOnTypes: {
        Call: 1,
      },
      times: 1,
      target_name: 'Jedis.java',
      source_label: 1,
      id: '1001',
      source: '1',
      source_name: 'Pipeline.java',
      target: '2',
      target_label: 2,
    },
    {
      dependsOnTypes: {
        Call: 3,
      },
      times: 3,
      target_name: 'Jedis.java',
      source_label: 3,
      id: '1002',
      source: '3',
      source_name: 'Transaction.java',
      target: '2',
      target_label: 2,
    },
    {
      dependsOnTypes: {
        Call: 3,
      },
      times: 3,
      target_name: 'Pipeline.java',
      source_label: 2,
      id: '1003',
      source: '2',
      source_name: 'Jedis.java',
      target: '1',
      target_label: 1,
    },
    {
      dependsOnTypes: {
        Call: 1,
      },
      times: 1,
      target_name: 'Transaction.java',
      source_label: 2,
      id: '1004',
      source: '2',
      source_name: 'Jedis.java',
      target: '3',
      target_label: 3,
    },
  ],
  smellType: 'CyclicDependency',
};
