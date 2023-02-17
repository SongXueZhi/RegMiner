import type { DdResultItems } from '../data';

// case 6
export const ddResult: DdResultItems = {
  allHunkEntities: [
    {
      newPath: 'src/main/java/com/univocity/parsers/common/input/AbstractCharInputReader.java',
      oldPath: 'src/main/java/com/univocity/parsers/common/input/AbstractCharInputReader.java',
      beginA: 397,
      beginB: 397,
      endA: 398,
      endB: 398,
      type: 'REPLACE',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/common/input/AbstractCharInputReader.java',
      oldPath: 'src/main/java/com/univocity/parsers/common/input/AbstractCharInputReader.java',
      beginA: 438,
      beginB: 438,
      endA: 438,
      endB: 506,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/common/input/CharInputReader.java',
      oldPath: 'src/main/java/com/univocity/parsers/common/input/CharInputReader.java',
      beginA: 30,
      beginB: 30,
      endA: 31,
      endB: 31,
      type: 'REPLACE',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/common/input/CharInputReader.java',
      oldPath: 'src/main/java/com/univocity/parsers/common/input/CharInputReader.java',
      beginA: 143,
      beginB: 143,
      endA: 143,
      endB: 161,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/common/input/LookaheadCharInputReader.java',
      oldPath: 'src/main/java/com/univocity/parsers/common/input/LookaheadCharInputReader.java',
      beginA: 235,
      beginB: 235,
      endA: 235,
      endB: 240,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 19,
      beginB: 19,
      endA: 19,
      endB: 20,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 55,
      beginB: 56,
      endA: 55,
      endB: 57,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 72,
      beginB: 74,
      endA: 72,
      endB: 75,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 120,
      beginB: 123,
      endA: 126,
      endB: 143,
      type: 'REPLACE',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 127,
      beginB: 144,
      endA: 127,
      endB: 146,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 154,
      beginB: 173,
      endA: 154,
      endB: 174,
      type: 'INSERT',
    },
    {
      newPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      oldPath: 'src/main/java/com/univocity/parsers/csv/CsvParser.java',
      beginA: 301,
      beginB: 321,
      endA: 302,
      endB: 322,
      type: 'REPLACE',
    },
  ],
  stepInfo: [
    {
      stepNum: 0,
      stepTestResult: 'FAIL',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: null,
      cprob: [0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1],
    },
    {
      stepNum: 1,
      stepTestResult: 'FAIL',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [10, 11],
      cprob: [
        0.153533993278763, 0.153533993278763, 0.153533993278763, 0.153533993278763,
        0.153533993278763, 0.153533993278763, 0.153533993278763, 0.153533993278763,
        0.153533993278763, 0.153533993278763, 0.1, 0.1,
      ],
    },
    {
      stepNum: 2,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [4, 5, 6, 7, 8, 9],
      cprob: [
        0.2628273061563664, 0.2628273061563664, 0.2628273061563664, 0.2628273061563664,
        0.153533993278763, 0.153533993278763, 0.153533993278763, 0.153533993278763,
        0.153533993278763, 0.153533993278763, 0.1711850910300794, 0.1711850910300794,
      ],
    },
    {
      stepNum: 3,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 3, 10, 11],
      cprob: [
        0.2628273061563664, 0.2628273061563664, 0.2628273061563664, 0.2628273061563664,
        0.24287145408993943, 0.24287145408993943, 0.24287145408993943, 0.24287145408993943,
        0.24287145408993943, 0.24287145408993943, 0.1711850910300794, 0.1711850910300794,
      ],
    },
    {
      stepNum: 4,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 3, 6, 7, 8, 9],
      cprob: [
        0.2628273061563664, 0.2628273061563664, 0.2628273061563664, 0.2628273061563664,
        0.40063294995636234, 0.40063294995636234, 0.24287145408993943, 0.24287145408993943,
        0.24287145408993943, 0.24287145408993943, 0.2823814279241393, 0.2823814279241393,
      ],
    },
    {
      stepNum: 5,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 3, 4, 5, 10, 11],
      cprob: [
        0.2628273061563664, 0.2628273061563664, 0.2628273061563664, 0.2628273061563664,
        0.40063294995636234, 0.40063294995636234, 0.3617432885553254, 0.3617432885553254,
        0.3617432885553254, 0.3617432885553254, 0.2823814279241393, 0.2823814279241393,
      ],
    },
    {
      stepNum: 6,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [3, 4, 5, 6, 7, 8, 9, 10, 11],
      cprob: [
        0.43848181777217876, 0.43848181777217876, 0.43848181777217876, 0.2628273061563664,
        0.40063294995636234, 0.40063294995636234, 0.3617432885553254, 0.3617432885553254,
        0.3617432885553254, 0.3617432885553254, 0.2823814279241393, 0.2823814279241393,
      ],
    },
    {
      stepNum: 7,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 4, 5, 6, 7, 8, 9],
      cprob: [
        0.43848181777217876, 0.43848181777217876, 0.43848181777217876, 0.4236598239714686,
        0.40063294995636234, 0.40063294995636234, 0.3617432885553254, 0.3617432885553254,
        0.3617432885553254, 0.3617432885553254, 0.4551797444363638, 0.4551797444363638,
      ],
    },
    {
      stepNum: 8,
      stepTestResult: 'PASS',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 3, 4, 5, 8, 9, 10, 11],
      cprob: [
        0.43848181777217876, 0.43848181777217876, 0.43848181777217876, 0.4236598239714686,
        0.40063294995636234, 0.40063294995636234, 0.0, 0.0, 0.3617432885553254, 0.3617432885553254,
        0.4551797444363638, 0.4551797444363638,
      ],
    },
    {
      stepNum: 9,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 3, 4, 5, 10, 11],
      cprob: [
        0.43848181777217876, 0.43848181777217876, 0.43848181777217876, 0.4236598239714686,
        0.40063294995636234, 0.40063294995636234, 0.0, 0.0, 0.6104049463152595, 0.6104049463152595,
        0.4551797444363638, 0.4551797444363638,
      ],
    },
    {
      stepNum: 10,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9, 10, 11],
      stepTestedInx: [0, 1, 2, 3, 8, 9, 10, 11],
      cprob: [
        0.43848181777217876, 0.43848181777217876, 0.43848181777217876, 0.4236598239714686,
        0.6252473439244078, 0.6252473439244078, 0.0, 0.0, 0.6104049463152595, 0.6104049463152595,
        0.4551797444363638, 0.4551797444363638,
      ],
    },
    {
      stepNum: 11,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9, 10, 11],
      stepTestedInx: [1, 2, 4, 5, 8, 9, 10, 11],
      cprob: [
        0.6482825860307713, 0.43848181777217876, 0.43848181777217876, 0.6263687002507938,
        0.6252473439244078, 0.6252473439244078, 0.0, 0.0, 0.6104049463152595, 0.6104049463152595,
        0.4551797444363638, 0.4551797444363638,
      ],
    },
    {
      stepNum: 12,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9, 10, 11],
      stepTestedInx: [0, 3, 4, 5, 8, 9, 10, 11],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 0.6263687002507938,
        0.6252473439244078, 0.6252473439244078, 0.0, 0.0, 0.6104049463152595, 0.6104049463152595,
        0.4551797444363638, 0.4551797444363638,
      ],
    },
    {
      stepNum: 13,
      stepTestResult: 'PASS',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9],
      stepTestedInx: [0, 1, 2, 3, 4, 5, 8, 9],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 0.6263687002507938,
        0.6252473439244078, 0.6252473439244078, 0.0, 0.0, 0.6104049463152595, 0.6104049463152595,
        0.0, 0.0,
      ],
    },
    {
      stepNum: 14,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9],
      stepTestedInx: [0, 1, 2, 3, 4, 5, 9],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 0.6263687002507938,
        0.6252473439244078, 0.6252473439244078, 0.0, 0.0, 1.0, 0.6104049463152595, 0.0, 0.0,
      ],
    },
    {
      stepNum: 15,
      stepTestResult: 'FAIL',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9],
      stepTestedInx: [0, 1, 2, 3, 4, 5, 8],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 0.6263687002507938,
        0.6252473439244078, 0.6252473439244078, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0,
      ],
    },
    {
      stepNum: 16,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 5, 8, 9],
      stepTestedInx: [0, 1, 2, 3, 5, 8, 9],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 0.6263687002507938, 1.0,
        0.6252473439244078, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0,
      ],
    },
    {
      stepNum: 17,
      stepTestResult: 'PASS',
      leftIdx2Test: [0, 1, 2, 3, 4, 8, 9],
      stepTestedInx: [0, 1, 2, 3, 4, 8, 9],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 0.6263687002507938, 1.0, 0.0,
        0.0, 0.0, 1.0, 1.0, 0.0, 0.0,
      ],
    },
    {
      stepNum: 18,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 8, 9],
      stepTestedInx: [0, 1, 2, 4, 8, 9],
      cprob: [
        0.6482825860307713, 0.6404024054163097, 0.6404024054163097, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0,
        1.0, 0.0, 0.0,
      ],
    },
    {
      stepNum: 19,
      stepTestResult: 'CE',
      leftIdx2Test: [0, 1, 2, 3, 4, 8, 9],
      stepTestedInx: [0, 2, 3, 4, 8, 9],
      cprob: [
        0.6482825860307713, 1.0, 0.6404024054163097, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0,
      ],
    },
    {
      stepNum: 20,
      stepTestResult: 'PASS',
      leftIdx2Test: [0, 1, 3, 4, 8, 9],
      stepTestedInx: [0, 1, 3, 4, 8, 9],
      cprob: [0.6482825860307713, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0],
    },
    {
      stepNum: 21,
      stepTestResult: 'PASS',
      leftIdx2Test: [1, 3, 4, 8, 9],
      stepTestedInx: [1, 3, 4, 8, 9],
      cprob: [0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0],
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
      label: 'Pipeline.java',
    },
    {
      path: '/src/main/java/redis/clients/jedis/Jedis.java',
      size: 30,
      name: 'Jedis.java',
      id: '2',
      label: 'Jedis.java',
    },
    {
      path: '/src/main/java/redis/clients/jedis/Transaction.java',
      size: 30,
      name: 'Transaction.java',
      id: '3',
      label: 'Transaction.java',
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
    // {
    //   dependsOnTypes: {
    //     Call: 3,
    //   },
    //   times: 3,
    //   target_name: 'Pipeline.java',
    //   source_label: 2,
    //   id: '1003',
    //   source: '2',
    //   source_name: 'Jedis.java',
    //   target: '1',
    //   target_label: 1,
    // },
    // {
    //   dependsOnTypes: {
    //     Call: 1,
    //   },
    //   times: 1,
    //   target_name: 'Transaction.java',
    //   source_label: 2,
    //   id: '1004',
    //   source: '2',
    //   source_name: 'Jedis.java',
    //   target: '3',
    //   target_label: 3,
    // },
  ],
  smellType: 'CyclicDependency',
};

export const relationTreeData = {
  id: 'Modeling Methods',
  children: [
    {
      id: 'Classification',
      children: [
        {
          id: 'Logistic regression',
        },
        {
          id: 'Linear discriminant analysis',
        },
        {
          id: 'Rules',
        },
        {
          id: 'Decision trees',
        },
        {
          id: 'Naive Bayes',
        },
        {
          id: 'K nearest neighbor',
        },
        {
          id: 'Probabilistic neural network',
        },
        {
          id: 'Support vector machine',
        },
      ],
    },
    // {
    //   id: 'Consensus',
    //   children: [
    //     {
    //       id: 'Models diversity',
    //       children: [
    //         {
    //           id: 'Different initializations',
    //         },
    //         {
    //           id: 'Different parameter choices',
    //         },
    //         {
    //           id: 'Different architectures',
    //         },
    //         {
    //           id: 'Different modeling methods',
    //         },
    //         {
    //           id: 'Different training sets',
    //         },
    //         {
    //           id: 'Different feature sets',
    //         },
    //       ],
    //     },
    //     {
    //       id: 'Methods',
    //       children: [
    //         {
    //           id: 'Classifier selection',
    //         },
    //         {
    //           id: 'Classifier fusion',
    //         },
    //       ],
    //     },
    //     {
    //       id: 'Common',
    //       children: [
    //         {
    //           id: 'Bagging',
    //         },
    //         {
    //           id: 'Boosting',
    //         },
    //         {
    //           id: 'AdaBoost',
    //         },
    //       ],
    //     },
    //   ],
    // },
    {
      id: 'Regression',
      children: [
        {
          id: 'Multiple linear regression',
        },
        {
          id: 'Partial least squares',
        },
        {
          id: 'Multi-layer feedforward neural network',
        },
        {
          id: 'General regression neural network',
        },
        {
          id: 'Support vector regression',
        },
      ],
    },
  ],
};

// Initial data
export const addNodeEdgeData = {
  nodes: [
    {
      id: 'node1',
      label: '1',
    },
    {
      id: 'node2',
      label: '2',
    },
    {
      id: 'node3',
      label: '3',
    },
  ],
  edges: [
    {
      id: 'edge1',
      target: 'node2',
      source: 'node1',
    },
  ],
};
