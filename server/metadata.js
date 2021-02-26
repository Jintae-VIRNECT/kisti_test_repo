'use strict'

const metaData = {
  ko: {
    title: 'VIRNECT | Remote',
    description:
      '산업 현장의 문제를 해결하는 AR 다자간 원격협업 솔루션 VIRNECT Remote입니다.',
    og: {
      title: 'VIRNECT Remote',
      description:
        '원격으로 현장 상황을 신속하고 정확하게 파악하고, AR 기능을 통해 정확한 업무 지시를 내릴 수 있습니다.',
      image: 'https://virnect.com/og_img.png',
      site_name: 'VIRNECT Remote',
      url: 'https://remote.virnect.com',
      type: 'website',
    },
  },
  en: {
    title: 'VIRNECT | Remote',
    description: 'Connect and begin your AR multi-location collaboration.',
    og: {
      title: 'VIRNECT Remote',
      description:
        'Enables rapid and accurate visibility of site conditions from remote locations. Work instructions can be given accurately through AR guide.',
      image: 'https://virnect.com/og_img.png',
      site_name: 'VIRNECT Remote',
      url: 'https://remote.virnect.com',
      type: 'website',
    },
  },
}
const metaHTML = lang => {
  const htmlText = `<html>
  <head>
    <title>${metaData[lang].title}</title>
    <meta name="description" content="${metaData[lang].description}">
    <meta property="og:title" content="${metaData[lang].og.title}">
    <meta property="og:description" content="${metaData[lang].og.description}">
    <meta property="og:image" content="${metaData[lang].og.image}">
    <meta property="og:site_name" content="${metaData[lang].og.site_name}">
    <meta property="og:url" content="${metaData[lang].og.url}">
    <meta property="og:type" content="${metaData[lang].og.type}">
  </head>
  </html>`
  return htmlText
}
const metaHEAD = (html, lang) => {
  const htmlText = html.replace(
    /<head>.*?<\/head>/gs,
    `<head>
    <meta charset="utf-8">
    <title>VIRNECT | REMOTE</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta
        name="viewport"
        content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
    <meta name="keywords" content="VIRNECT Remote,원격협업,AR,증강현실">
    <meta name="theme-color" content="#0064ff">
    <title>${metaData[lang].title}</title>
    <meta name="description" content="${metaData[lang].description}">
    <meta property="og:title" content="${metaData[lang].og.title}">
    <meta property="og:description" content="${metaData[lang].og.description}">
    <meta property="og:image" content="${metaData[lang].og.image}">
    <meta property="og:site_name" content="${metaData[lang].og.site_name}">
    <meta property="og:url" content="${metaData[lang].og.url}">
    <meta property="og:type" content="${metaData[lang].og.type}">
    <link rel="shortcut icon" href="/favicon.ico">
    </head>`,
  )
  return htmlText
}

module.exports = { metaHTML, metaHEAD }
