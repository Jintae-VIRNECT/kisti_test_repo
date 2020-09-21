const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./configs/runtime')

/**
 * @description
 * Main app route
 */

router.get('/urls', (req, res) => {
	res.header('Content-Type', 'application/json')
	;(async () => {
		const response = await config.init()
		if (response) {
			res.send(JSON.stringify(config.getUrls()))
			res.send(JSON.stringify(config.getEnv()))
		}
	})()
})

router.get('/healthcheck', (req, res) => {
	res.send('Hi Virnect')
})

router.get('/*', function(req, res) {
	res.sendFile(path.join(__dirname, '/dist/app.html'))
})

module.exports = router
