const express = require('express')
const router = express.Router()
const path = require('path')

/**
 * @description
 * Main app route
 */

router.get('/find/email', (req, res) => {
	res.sendFile(
		path.join(__dirname, '../src/main/resources/templates/index.html'),
	)
})

router.get('/find/reset_password', (req, res) => {
	res.sendFile(
		path.join(__dirname, '../src/main/resources/templates/index.html'),
	)
})

router.get('/healthcheck', (req, res) => {
	res.send('Hi Virnect')
})

router.get('/*', function(req, res) {
	res.sendFile(
		path.join(__dirname, '../src/main/resources/templates/index.html'),
	)
})

module.exports = router
