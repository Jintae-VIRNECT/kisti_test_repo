import { fabric as Fabric } from 'fabric'

/* @Override */
Fabric.Object.prototype._drawControl = function(
  control,
  ctx,
  methodName,
  left,
  top,
  styleOverride,
) {
  styleOverride = styleOverride || {}
  if (!this.isControlVisible(control)) {
    return
  }

  var size = this.cornerSize,
    stroke = !this.transparentCorners && this.cornerStrokeColor

  switch (styleOverride.cornerStyle || this.cornerStyle) {
    case 'circle':
      ctx.beginPath()
      ctx.arc(left + size / 2, top + size / 2, size / 2, 0, 2 * Math.PI, false)
      ctx[methodName]()
      if (stroke) {
        ctx.stroke()
      }
      break
    default:
      this.transparentCorners || ctx.clearRect(left, top, size, size)
      ctx[methodName + 'Rect'](left, top, size, size)
      if (stroke) {
        ctx.strokeRect(left, top, size, size)
      }
  }
}

/* @Override */
Fabric.Object.prototype._renderStroke = function(ctx) {
  if (!this.stroke || this.strokeWidth === 0) {
    return
  }
  if (this.shadow && !this.shadow.affectStroke) {
    this._removeShadow(ctx)
  }
  ctx.save()
  ctx.scale(1 / this.scaleX, 1 / this.scaleY)
  // this._setLineDash(ctx, this.strokeDashArray, this._renderDashedStroke);
  // this._applyPatternGradientTransform(ctx, this.stroke);
  ctx.stroke()
  ctx.restore()
}

/* @Override */
Fabric.Canvas.prototype._getActionFromCorner = function(target, corner, e) {
  var action = 'drag'
  if (corner) {
    switch (corner) {
      case 'mtr':
        action = 'rotate'
        break
      case 'ml':
      case 'mr':
        action = 'scaleX'
        break
      case 'mt':
        action = 'delete'
        // this.remove(this.getActiveObject());
        break
      case 'mb':
        action = 'scaleY'
        break
      default:
        action = 'scale'
    }
  }

  return action
}

/* @Override */
Fabric.Canvas.prototype._performTransformAction = function(
  e,
  transform,
  pointer,
) {
  var x = pointer.x,
    y = pointer.y,
    action = transform.action,
    actionPerformed = false,
    options = {
      target: transform.target,
      e: e,
      transform: transform,
      pointer: pointer,
    }

  if (action === 'rotate') {
    ;(actionPerformed = this._rotateObject(x, y)) &&
      this._fire('rotating', options)
  } else if (action === 'scale') {
    //텍스트 스케일 기능 제거
    if (false === transform.target.hasOwnProperty('text')) {
      ;(actionPerformed = this._onScale(e, transform, x, y)) &&
        this._fire('scaling', options)
    }
  } else if (action === 'scaleX') {
    //텍스트 스케일 기능 제거
    if (false === transform.target.hasOwnProperty('text')) {
      ;(actionPerformed = this._scaleObject(x, y, 'x')) &&
        this._fire('scaling', options)
    }
  } else if (action === 'scaleY') {
    //텍스트 스케일 기능 제거
    if (false === transform.target.hasOwnProperty('text')) {
      ;(actionPerformed = this._scaleObject(x, y, 'y')) &&
        this._fire('scaling', options)
    }
  } else if (action === 'skewX') {
    ;(actionPerformed = this._skewObject(x, y, 'x')) &&
      this._fire('skewing', options)
  } else if (action === 'skewY') {
    ;(actionPerformed = this._skewObject(x, y, 'y')) &&
      this._fire('skewing', options)
  } else if (action === 'delete') {
    //do nothing
  } else {
    actionPerformed = this._translateObject(x, y)
    if (actionPerformed) {
      this._fire('moving', options)
      this.setCursor(options.target.moveCursor || this.moveCursor)
    }
  }
  transform.actionPerformed = transform.actionPerformed || actionPerformed
}

// Canvas - Select options.
Fabric.Canvas.prototype.selection = false
Fabric.Canvas.prototype.cursorMap = [
  'pointer',
  'ne-resize',
  'e-resize',
  'se-resize',
  's-resize',
  'sw-resize',
  'w-resize',
  'nw-resize',
]

// Object options.
Fabric.Object.prototype.flipX = false
Fabric.Object.prototype.flipY = false
Fabric.Object.prototype.lockSkewingX = true
Fabric.Object.prototype.lockSkewingY = true
Fabric.Object.prototype.lockRotation = true
Fabric.Object.prototype.lockScalingFlip = true
Fabric.Object.prototype.lockUniScaling = false
Fabric.Object.prototype.setControlsVisibility({
  tl: true,
  tr: true,
  bl: true,
  br: true,
  ml: false,
  // 'mt':true,
  mt: false,
  mr: false,
  mb: false,
  mtr: false,
})

// Object - Corner options.
Fabric.Object.prototype.cornerSize = 6
Fabric.Object.prototype.cornerColor = '#FFFFFF'
Fabric.Object.prototype.cornerStrokeColor = '#000000'
Fabric.Object.prototype.transparentCorners = false

// Object - Select options.
Fabric.Object.prototype.selectable = false
Fabric.Object.prototype.hasControls = true
Fabric.Object.prototype.hasBorders = true
Fabric.Object.prototype.borderColor = '#0054f7'

// Image - Options
Fabric.Image.prototype.lockScalingFlip = false
Fabric.Image.prototype.lockRotation = false
Fabric.Image.prototype.setControlVisible('mtr', true)

export const fabric = Fabric
