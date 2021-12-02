import path from 'path'
import glob from 'glob'
import Vue from 'vue'

function capitalizeFirstLetter(folderName) {
  return folderName.charAt(0).toUpperCase() + folderName.slice(1)
}

export default function beforeAllTests() {
  const fileComponents = glob.sync(
    path.join(__dirname, '../src/components/**/*.vue'),
  )

  for (const file of fileComponents) {
    const match = file.match(/(\w*)\/(\w*)\.vue$/)
    const folderName = match[1]
    const fileName = match[2]
    const componentName = capitalizeFirstLetter(folderName) + fileName
    Vue.component(componentName, require(file).default)
  }
}
