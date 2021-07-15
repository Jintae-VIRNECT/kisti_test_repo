const helmet = require('helmet')
const config = require('./config')

const initHelmet = async app => {
  await config.init()
  if (config.getEnv() === 'onpremise') {
    return
  }

  const urls = config.getUrls()
  const allows = []
  const cntAllows = []

  for (const property in urls) {
    if (property.includes('minio')) {
      allows.push(urls[property])
    }

    if (property.includes('csp.wss')) {
      cntAllows.push(urls[property])
    } else if (property.includes('csp.')) {
      allows.push(urls[property])
    }
  }

  cntAllows.push(...allows)

  app.use(
    helmet({
      frameguard: {
        action: 'deny',
      },
      contentSecurityPolicy: {
        directives: {
          ...helmet.contentSecurityPolicy.getDefaultDirectives(),
          'script-src': ["'self'", ...allows],
          'style-src': [
            "'self'",
            'https:',
            'blob:',
            "'unsafe-inline'",
            ...allows,
          ],
          'connect-src': ["'self'", 'blob:', 'data:', ...cntAllows],
          'img-src': ["'self'", 'blob:', 'data:', ...allows],
          'media-src': ["'self'", 'data:', ...allows],
          'worker-src': ["'self'", 'blob:', ...allows],
          'child-src': ['blob:', ...allows],
        },
      },
    }),
  )
}

module.exports = { initHelmet }
