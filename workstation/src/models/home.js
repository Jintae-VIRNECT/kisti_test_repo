export function install(app) {
  return [
    {
      label: app.$t('home.install.remote'),
      path: `${app.$url.download}/remote`,
      image: require('assets/images/logo/logo-remote.svg'),
      isLink: true,
    },
    {
      label: app.$t('home.install.make'),
      path: `${app.$url.download}/make`,
      image: require('assets/images/logo/logo-make.svg'),
      isLink: true,
    },
    {
      label: app.$t('home.install.view'),
      path: `${app.$url.download}/view`,
      image: require('assets/images/logo/logo-view.svg'),
      isLink: true,
    },
  ]
}

export function guide(app) {
  return [
    {
      label: app.$t('guide.makeGuide.label'),
      path: app.$t('guide.makeGuide.url'),
    },
    {
      label: app.$t('guide.makeDemoContetns.label'),
      path: app.$t('guide.makeDemoContetns.url'),
    },
    {
      label: app.$t('guide.viewGuide.label'),
      path: app.$t('guide.viewGuide.url'),
    },
    {
      label: app.$t('guide.viewRealwearGuide.label'),
      path: app.$t('guide.viewRealwearGuide.url'),
    },
    {
      label: app.$t('guide.remoteGuide.label'),
      path: app.$t('guide.remoteGuide.url'),
    },
    {
      label: app.$t('guide.workstationGuide.label'),
      path: app.$t('guide.workstationGuide.url'),
    },
    {
      label: app.$t('guide.purchaseGuide.label'),
      path: app.$t('guide.purchaseGuide.url'),
    },
  ]
}
