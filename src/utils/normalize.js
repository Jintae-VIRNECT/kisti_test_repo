export const normalizedPosX = (posX, width) => {
  return (2 * posX) / width - 1
}

export const normalizedPosY = (posY, height) => {
  return -((2 * posY) / height - 1)
}

export const originalPosX = (posX, width) => {
  return (Math.abs(posX + 1) * width) / 2
}

export const originalPosY = (posY, height) => {
  return (Math.abs(posY - 1) * height) / 2
}
