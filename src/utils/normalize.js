export const normalizedPos = (pos, length) => {
  return (2 * pos) / length - 1
}

export const originalPos = (normal, length) => {
  return (Math.abs(normal + 1) * length) / 2
}
