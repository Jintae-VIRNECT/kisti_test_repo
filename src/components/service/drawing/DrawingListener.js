import { fabric } from 'plugins/remote/fabric.custom'
import { ahexToRGBA } from 'utils/color'
import { getReceiveParams, calcPosition } from 'utils/drawing'
import { SIGNAL, DRAWING } from 'configs/remote.config'

export default {
  data() {
    return {
      pathAId: null, //현재 그려지는 드로잉 고유 id, 같은 참가자가 그린 드로잉내에서도 구분하기 위함

      // 현재 그려지는 드로잉 경로
      receivePath: {
        // connectionId: [
        //   ['M', posX, posY] // 경로
        // ]
      },
      // 캔버스 초기화 전 받은 드로잉 정보
      receivedList: {
        // connectionId: [{
        //   id: 0,
        //   owner: owner,
        //   data: {},
        //   path: [] / {},
        // }]
      },
    }
  },
  methods: {
    drawingListener({ data, receive }) {
      if (data.type === DRAWING.FILE_SHARE) {
        this.receivedList[receive.from.connectionId] = []
        for (let key in this.receivePath) {
          delete this.receivePath[key]
        }
        this.isInit = false
        return
      }

      if (receive.from.connectionId === this.myInfo.connectionId) return //본인 시그날 데이터는 제외
      // if (this.account.roleType === ROLE.LEADER) return

      //드로잉 시그날 외에 시그날 데이터 제외
      if (
        ![
          DRAWING.LINE_DOWN,
          DRAWING.LINE_MOVE,
          DRAWING.LINE_UP,
          DRAWING.TEXT_ADD,
          DRAWING.UNDO,
          DRAWING.REDO,
          DRAWING.CLEAR,
          DRAWING.CLEAR_ALL,
        ].includes(data.type)
      )
        return

      //현재 view가 협업보드 드로이고, canvas 초기화 완료된 경우 - canvas에 동작 실행
      if (this.isDrawingView && this.isInit) {
        this.addReceiveObject({ data, owner: receive.from.connectionId }) //수신한 시그날 실행 (드로잉 객체 추가, undo/redo, clear/clearAll)
      }
      //receivedList에 쌓아둠
      else {
        if (!this.receivedList[receive.from.connectionId]) {
          this.receivedList[receive.from.connectionId] = []
        }
        this.receivedList[receive.from.connectionId].push({
          data,
          owner: receive.from.connectionId,
        })
        console.error(this.receivedList)
      }
    },
    addReceiveObject({ data, owner }) {
      switch (data.type) {
        case DRAWING.LINE_DOWN:
        case DRAWING.LINE_MOVE:
        case DRAWING.LINE_UP:
          this.drawingLine(data, owner)
          break
        case DRAWING.TEXT_ADD:
          this.drawingText(data, owner)
          break
        case DRAWING.UNDO:
          this.receiveStackUndo(owner)
          break
        case DRAWING.REDO:
          this.receiveStackRedo(owner)
          break
        case DRAWING.CLEAR:
          this.clear(owner)
          break
        case DRAWING.CLEAR_ALL:
          this.clearAll()
          break
      }
    },
    drawingLine(data, owner) {
      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
        imgWidth: this.canvas.getWidth(),
        imgHeight: this.canvas.getHeight(),
      }

      // if (data.type === DRAWING.LINE_DOWN) {
      //   this.receivePath[owner] = []
      // }

      //수신 드로잉 경로에 해당 유저 Array 없는 경우 배열 생성 초기화
      if (!(owner in this.receivePath)) {
        if (this.pathAId !== data.aId || this.owner !== owner)
          this.receivePath[owner] = []
      }

      this.pathAId = data.aId
      this.owner = owner

      //드로잉의 경우 position 배열, text의 경우 object를 필요한 부분 가공해서 반환
      let receiveParams = getReceiveParams(data.type, params, this.origin.scale)

      //드로잉 시작, 경로 array에 저장
      this.receivePath[owner].push(receiveParams)

      //드로잉 끝나는 이벤트인 경우 - 드로잉 객체 canvas에 추가
      if (data.type === DRAWING.LINE_UP) {
        const width =
          parseFloat(data.width) * (this.origin.width / this.img.width)
        // const width = parseInt(data.width)
        console.error('path', this.receivePath[owner])
        const pos = calcPosition(this.receivePath[owner], width)
        const path = new fabric.Path(this.receivePath[owner], {
          left: pos.left,
          top: pos.top,
          fill: null,
          stroke: ahexToRGBA(data.color), //data.color,
          strokeWidth: width,
          strokeMiterLimit: width,
          strokeLineCap: 'round',
          strokeLineJoin: 'round',
          owner: owner,
          hasControls: false,
          selectable: false,
          hoverCursor: 'auto',
        })
        // path.set()
        this.canvas.add(path)
        //this.canvas.renderAll() //해당 함수로 호출로 인해 기존 창 크기 변화 시 드로잉 객체 크기 변화 이슈가 발생됨. 해당 함수 호출 이유 불분명
        this.backCanvas.add(fabric.util.object.clone(path))
        this.backCanvas.renderAll()
        this.$nextTick(() => {
          delete this.receivePath[owner]
        })

        this.receivePath[owner] = []
      }
    },
    drawingText(data, owner) {
      const params = getReceiveParams(
        DRAWING.TEXT_ADD,
        {
          ...data,
          scale: 1 / this.canvas.backgroundImage.scaleX,
          imgWidth: this.canvas.getWidth(),
          imgHeight: this.canvas.getHeight(),
          sizeScale: this.origin.width / this.img.width,
        },
        this.origin.scale,
      )
      const object = new fabric.IText(data.text, {
        left: params.posX,
        top: params.posY,
        fill: ahexToRGBA(params.color),
        fontFamily: this.fontFamily,
        fontStyle: this.fontStyle,
        fontWeight: this.fontWeight,
        fontSize: params.size,
        lineHeight: this.lineHeight,
        hasControls: false,
        selectable: false,
        hoverCursor: 'auto',
        owner: owner,
      })
      this.canvas.add(object)
      //this.canvas.renderAll() //해당 함수로 호출로 인해 기존 창 크기 변화 시 드로잉 객체 크기 변화 이슈가 발생됨. 해당 함수 호출 이유 불분명
      this.backCanvas.add(fabric.util.object.clone(object))
      this.backCanvas.renderAll()
    },
    clear(owner) {
      this.canvas.getObjects().forEach(object => {
        if (object.owner === owner) {
          object.canvas.remove(object)
        }
      })
      this.backCanvas.getObjects().forEach(object => {
        if (object.owner === owner) {
          object.canvas.remove(object)
        }
      })
      this.canvas.renderAll()
      this.backCanvas.renderAll()
      delete this.receiveUndoList[owner]
      delete this.receiveRedoList[owner]

      this.toolAble()
    },
    clearAll() {
      this.canvas.remove(...this.canvas.getObjects())
      this.backCanvas.remove(...this.backCanvas.getObjects())
      this.canvas.renderAll()
      this.backCanvas.renderAll()
      this.stackClear()
      this.receivedStackClear()
    },
  },

  /* Lifecycles */
  created() {
    if (this.$call) {
      this.$eventBus.$on(SIGNAL.DRAWING, this.drawingListener) //시그널 데이터 수신시 바로 발생되는 이벤트 리스너
      this.$eventBus.$on(SIGNAL.DRAWING_FROM_VUEX, this.drawingListener) //vuex 큐에 임시 저장해두었다가 발생시키는 이벤트 리스너
    }
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.DRAWING, this.drawingListener)
    this.$eventBus.$off(SIGNAL.DRAWING_FROM_VUEX, this.drawingListener)
  },
}
