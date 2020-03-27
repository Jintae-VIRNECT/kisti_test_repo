const express = require('express')
const router = express.Router()
const path = require('path')

/**
 * @description
 * Main app route
 */
router.get('/*', function(req, res) {
	res.sendFile(path.join(__dirname, '/dist/main/main.html'))
})

module.exports = router
