import { LikeFilled, LikeOutlined, DislikeFilled, DislikeOutlined } from '@ant-design/icons';
import { Tag, Tooltip, message } from 'antd';
import { useAccess } from 'umi';
import { agreeBugType, disagreeBugType } from '../service';
import { useState } from 'react';

interface IProps {
  bugTypeId: number;
  bugTypeName: string;
  agreeCount: number;
  disagreeCount: number;
  regressionUuid: string;
  onUpdateData: ({
    agreeCount,
    disagreeCount,
  }: {
    agreeCount?: number;
    disagreeCount?: number;
  }) => void;
}

const BugType: React.FC<IProps> = ({
  bugTypeId,
  bugTypeName,
  agreeCount,
  disagreeCount,
  regressionUuid,
  onUpdateData,
}) => {
  const access = useAccess();
  const [agree, setAgree] = useState<boolean | undefined>(undefined);

  return (
    <Tag color="purple" className={`${bugTypeId}-bugType-action`}>
      {bugTypeName}
      <Tooltip key="comment-basic-agree" title="Agree">
        {/* <Button */}
        <span
          // size="small"
          className={`${bugTypeId}-bugType-action`}
          onClick={() => {
            if (access.allUsersFoo) {
              if (agree !== true) {
                setAgree(true);
                agreeBugType({
                  regression_uuid: regressionUuid,
                  bug_type_id: bugTypeId,
                }).then(() => {
                  onUpdateData({});
                });
              } else {
                setAgree(undefined);
              }
            } else {
              message.error(
                'Sorry, you have no right to do that. Please login or use another account!',
              );
            }
          }}
          style={{ marginLeft: 5 }}
        >
          {agree === true ? <LikeFilled /> : <LikeOutlined />}
          <span>{agreeCount}</span>
          {/* </Button> */}
        </span>
      </Tooltip>
      <Tooltip key="comment-basic-disagree" title="Disagree">
        {/* <Button */}
        <span
          // size="small"
          onClick={() => {
            if (access.allUsersFoo) {
              if (agree !== false) {
                setAgree(false);
              } else {
                setAgree(undefined);
              }
              disagreeBugType({
                regression_uuid: regressionUuid,
                bug_type_id: bugTypeId,
              }).then(() => {
                onUpdateData({});
              });
            } else {
              message.error(
                'Sorry, you have no right to do that. Please login or use another account!',
              );
            }
          }}
          style={{ marginLeft: 5 }}
        >
          {agree === false ? <DislikeFilled /> : <DislikeOutlined />}
          <span>{disagreeCount}</span>
          {/* </Button> */}
        </span>
      </Tooltip>
    </Tag>
  );
};

export default BugType;
