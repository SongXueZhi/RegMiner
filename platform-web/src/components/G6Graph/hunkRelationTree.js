import G6 from '@antv/g6';

const drawRelationTreeGraph = function (json_data, graphId) {
  const container = document.getElementById(graphId);
  const descriptionDiv = document.createElement('div');
  descriptionDiv.innerHTML =
    'Move a subtree to a new parent by dragging the root node of the subtree.';
  container.appendChild(descriptionDiv);

  const nodeTip = new G6.Tooltip({
    offsetX: 10,
    offsetY: -15,
    fixToNode: [1, 0],
    trigger: 'click',
    itemTypes: ['node'],
    getContent: (e) => {
      const outDiv = document.createElement('div');
      outDiv.style.width = 'fit-content';
      outDiv.innerHTML = `
              <h5>name: ${e.item.getModel().name}</h5>
              <h5>path: ${e.item.getModel().path}</h5>`;
      return outDiv;
    },
  });
  const edgeTip = new G6.Tooltip({
    offsetX: 10,
    offsetY: 0,
    fixToNode: [1, 0],
    trigger: 'click',
    itemTypes: ['edge'],
    getContent: (e) => {
      const outDiv = document.createElement('div');
      switch (smellType) {
        case CYCLIC_DEPENDENCY:
          let dependsOnTypes = e.item.getModel().dependsOnTypes;
          let str = ``;
          for (let key in dependsOnTypes) {
            str += `<ul><li>${key}: ${dependsOnTypes[key]}</li></ul>`;
          }
          outDiv.style.width = 'fit-content';
          outDiv.innerHTML =
            `
                          <h5>Source: (${e.item.getModel().source_label})${
              e.item.getModel().source_name
            }</h5>
                          <h5>Target: (${e.item.getModel().target_label})${
              e.item.getModel().target_name
            }</h5>
                          <h5>Relation: DependsOn(${e.item.getModel().times})</h5>` + str;
          break;
        case UNUSED_INCLUDE:
          outDiv.style.width = 'fit-content';
          outDiv.innerHTML = `
              <h5>Source: (${e.item.getModel().source_label})${e.item.getModel().source_name}</h5>
              <h5>Target: (${e.item.getModel().target_label})${e.item.getModel().target_name}</h5>
              <h5>Relation: Include</h5>`;
          break;
        default:
          break;
      }
      return outDiv;
    },
  });

  // eslint-disable-next-line @typescript-eslint/no-shadow
  const width = container.scrollWidth;
  const height = (container.scrollHeight || 500) - 20;
  const graph = new G6.TreeGraph({
    container: graphId,
    width,
    height,
    modes: {
      default: [
        'drag-canvas',
        'zoom-canvas',
        {
          type: 'drag-node',
          enableDelegate: true,
        },
      ],
    },
    plugins: [nodeTip, edgeTip],
    animate: true,
    defaultNode: {
      //   type: 'rect',
      size: [26, 26],
      anchorPoints: [
        [0, 0.5],
        [1, 0.5],
      ],
      style: {
        lineWidth: 1,
        fill: '#C6E5FF',
        stroke: '#5B8FF9',
      },
    },
    // defaultEdge: {
    //   type: 'cubic-horizontal',
    //   style: {
    //     stroke: '#A3B1BF',
    //   },
    // },
    defaultEdge: {
      size: 1,
      color: '#A9A9A9',
      style: {
        endArrow: {
          path: 'M 0,0 L 8,4 L 8,-4 Z',
          fill: '#A9A9A9',
        },
      },
      labelCfg: {
        autoRotate: true,
      },
    },
    nodeStateStyles: {
      closest: {
        fill: '#f00',
      },
    },
    layout: {
      type: 'compactBox',
      direction: 'LR',
      getId: function getId(d) {
        return d.id;
      },
      getHeight: function getHeight() {
        return 16;
      },
      getWidth: function getWidth() {
        return 16;
      },
      getVGap: function getVGap() {
        return 10;
      },
      getHGap: function getHGap() {
        return 100;
      },
    },
  });

  graph.node(function (node) {
    return {
      label: node.id,
      labelCfg: {
        offset: 10,
        position: node.children && node.children.length > 0 ? 'left' : 'right',
      },
    };
  });

  graph.data(json_data);
  graph.render();
  graph.fitView();

  let minDisNode;
  graph.on('node:dragstart', () => {
    minDisNode = undefined;
  });
  graph.on('node:drag', (e) => {
    minDisNode = undefined;
    const item = e.item;
    const model = item.getModel();
    const nodes = graph.getNodes();
    let minDis = Infinity;
    nodes.forEach((inode) => {
      graph.setItemState(inode, 'closest', false);
      const node = inode.getModel();
      if (node.id === model.id) return;
      const dis = (node.x - e.x) * (node.x - e.x) + (node.y - e.y) * (node.y - e.y);
      if (dis < minDis) {
        minDis = dis;
        minDisNode = inode;
      }
    });
    console.log('minDis', minDis, minDisNode);
    if (minDis < 2000) graph.setItemState(minDisNode, 'closest', true);
    else minDisNode = undefined;
  });

  graph.on('node:dragend', (e) => {
    if (!minDisNode) {
      descriptionDiv.innerHTML = 'Failed. No node close to the dragged node.';
      return;
    }
    const item = e.item;
    const id = item.getID();
    const NodeDetail = graph.findDataById(id);
    // if the minDisNode is a descent of the dragged node, return
    let isDescent = false;
    const minDisNodeId = minDisNode.getID();
    console.log('dragend', minDisNodeId, isDescent, NodeDetail, id);

    G6.Util.traverseTree(NodeDetail, (d) => {
      if (d.id === minDisNodeId) isDescent = true;
    });
    if (isDescent) {
      descriptionDiv.innerHTML = 'Failed. The target node is a descendant of the dragged node.';
      return;
    }
    graph.removeChild(id);

    setTimeout(() => {
      const newParentData = graph.findDataById(minDisNodeId);
      let newChildren = newParentData.children;
      if (newChildren) newChildren.push(NodeDetail);
      else newChildren = [NodeDetail];
      graph.updateChildren(newChildren, minDisNodeId);
      descriptionDiv.innerHTML = 'Success.';
    }, 600);
  });

  if (typeof window !== 'undefined')
    window.onresize = () => {
      if (!graph || graph.get('destroyed')) return;
      if (!container || !container.scrollWidth || !container.scrollHeight) return;
      graph.changeSize(container.scrollWidth, container.scrollHeight - 20);
    };
};

export {drawRelationTreeGraph};
