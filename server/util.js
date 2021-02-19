'use strict'
const IsAllowBrowser = req => {
  const userAgent = req.headers['user-agent'] || ''
  const isChrome = userAgent.includes('Chrome')
  const isChromeMobile =
    userAgent.includes('CriOS') || userAgent.includes('mobileApp')
  const isEdge = userAgent.includes('Edg') || userAgent.includes('Edge')
  const isSamsung = userAgent.includes('SamsungBrowser')

  const isSafari = !isChrome && !isChromeMobile && userAgent.includes('Safari')

  return ((isChrome || isEdge || isChromeMobile) && !isSamsung) || isSafari
}

const IsMobileBrowser = req => {
  const userAgent = req.headers['user-agent'] || ''
  const isChromeMobile =
    userAgent.includes('Mobile') ||
    userAgent.includes('CriOS') ||
    userAgent.includes('mobileApp') ||
    userAgent.includes('iPhone')

  return isChromeMobile
}

const GenMetaHTML = metaObj => {
  const htmlText = `<html>
  <head>
    <title>${metaObj.head.title}</title>
    <meta name="description" content="${metaObj.head.description}">
    <meta property="og:description" content="${metaObj.head.og.description}">
    <meta property="og:image" content="${metaObj.head.og.image}">
    <meta property="og:site_name" content="${metaObj.head.og.site_name}">
    <meta property="og:url" content="${metaObj.head.og.url}">
    <meta property="og:type" content="${metaObj.head.og.type}">
  </head>
  </html>`
  return htmlText
}

module.exports = {
  IsAllowBrowser,
  IsMobileBrowser,
  GenMetaHTML,
}
