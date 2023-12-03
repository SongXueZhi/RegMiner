import {Typography} from 'antd';

const {Text} = Typography;

const EllipsisMiddle = ({suffixCount, children}: { suffixCount: number; children: any }) => {
  const start = children.slice(0, children.length - suffixCount).trim();
  const suffix = children.slice(-suffixCount).trim();
  return (
    <Text style={{maxWidth: '100%'}} ellipsis={{suffix}}>
      {start}
    </Text>
  );
};

export default EllipsisMiddle;
