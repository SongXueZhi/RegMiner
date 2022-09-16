import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-layout';

const Footer: React.FC = () => {
  // const intl = useIntl();
  // const defaultMessage = intl.formatMessage({
  //   id: 'app.copyright.produced',
  //   defaultMessage: 'Richy',
  // });

  const currentYear = new Date().getFullYear();
  const defaultMessage = 'David';

  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'left',
          title: 'RegMiner Data Annotations',
          href: 'https://sites.google.com/d/1czj_Xo96OgTOoH0EffWvzte03TJtiv5K/p/1t7VGB1k5rRObf-3O1SgApJXShFMcGJT3/edit',
          blankTarget: true,
        },
        {
          key: 'icon',
          title: <GithubOutlined />,
          href: 'https://github.com/SongXueZhi/RegMiner',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
