// Imports the Google Cloud client library
const { Translate } = require('@google-cloud/translate').v2

let translate = null

// add process env GOOGLE_APPLICATION_CREDENTIALS=./translate/cloudauth.json

// const translate = new Translate({ projectId: 'nodal-buckeye-270902' })

/**
 * TODO(developer): Uncomment the following lines before running the sample.
 */
// const text = 'Hello, world!'
// const target = 'ko'

// Translates the text into the target language. "text" can be a string for
// translating a single piece of text, or an array of strings for translating
// multiple texts.

async function getTranslate(text, target) {
  if (translate === null) {
    translate = new Translate({
      projectId: 'nodal-buckeye-270902',
      keyFilename: './translate/remote-translation.json',
    })
  }
  // The text to translate
  // const text = 'Hello, world!';

  // The target language
  // const target = 'ru';

  // Translates some text into Russian
  try {
    console.log(`Text: ${text}`)
    const [translation] = await translate.translate(text, target)
    console.log(`Translation: ${translation}`)
    return translation
  } catch (err) {
    console.log(err)
  }
}

module.exports = {
  getTranslate,
}
