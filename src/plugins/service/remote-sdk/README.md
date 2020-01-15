# Remote SDK datachannel guide

## 용어 정의

| 용어 | 타입 | 설명 | 예시 |
|------|------|------|------|
| imgId | Number | 대상 이미지 ID  |
| aId   | Number | 히스토리를 위한 |
| tId   | Number | 대상 Object ID  |
| type  | String | 요청 타입 ( drawMove, lineStart... ) |

---

## 전송형태 예시
### Pointing
```JSON
{
    "type": "pointing",
    "color" : String, //HexCode(ARGB) -> ex) #ffcc0a04
    "width": Number,
    "posX" : Number,
    "posY" : Number
} 
```

### Image
1. Start image
```JSON
{
     "type": "startImage" 
}
```

2. Show image (*current support image format : jpg, jpeg, png)
> Check response param.
```JSON
{ //Request
    "type": "showImage",
    "imgId" : Number,
    "imgData" : "Base64 Enconded image string",
    "ext" : String // (jpg, png, svg...)

}
{ //Response
    "type": "showImage",
    "imgId": Number,
    "result": Boolean
}
```

3. End image (=Hide image)
```JSON
{
    "type": "endImage"
}
```

### Object Create
#### Line drawing
> Example of drawing a single line.

1. mouse down event (start point) 
```JSON
{
    "type": "drawLineDown",
    "aId": Number,
    "color" : String, //HexCode(ARGB) -> ex) #ffcc0a04
    "width": Number,
    "posX" : Number,
    "posY" : Number
} 
```

2. mouse move event (moving point) 
```JSON
{
    "aId": Number,
    "type": "drawLineMove",
    "color" : String, //HexCode(ARGB) -> ex) #ffcc0a04
    "width": Number,
    "posX" : Number,
    "posY" : Number

}
```

3. mouse up event (end point)
```JSON
{
    "type": "drawLineUp",
    "aId": Number,
    "color" : String, //HexCode(ARGB) -> ex) #ffcc0a04
    "width": Number,
    "posX" : Number,
    "posY" : Number

}
```

#### Text drawing

1. Text add
```JSON
{
    "type": "drawText",
    "aId": Number,
    "text": String,
    "color" : String, //HexCode(ARGB) -> ex) #ffcc0a04
    "size": Number,
    "posX" : Number,
    "posY" :Number,
    "width": Number,
    "height": Number
}
```

2. Text update
```JSON
{
    "type": "updateText",
    "aId": Number,
    "tId": Number,
    "text": String
}
```

### Object History
1. Undo
```JSON
{
    "type":"drawUndo"
}
```

2. Redo
```JSON
{
    "type": "drawRedo"
}
```

3. Delete all objects
```JSON
{
    "type": "drawClearAll",
    "imgId": Number
}
```

4. Delete target object
```JSON
{
    "type": "drawClear",
    "imgId": Number,
    "aId" : Number,
    "oId" : Number,
    "tId" : Number
}
```

### Object Modify
#### Move
```JSON
{
    "type": "drawMove",
    "aId": Number,
    "tId": Number,
    "posX" : Number,
    "posY" : Number
}
```

#### ~~Scale~~
1. ~~Scale up~~
```JSON
{
    "type": "drawScale",
    "aId": Number,
    "tId": Number,
    "scaleX": Number,
    "scaleY": Number,
    "posX" : Number,
    "posY" : Number
}
```
2. ~~Scale down~~
```JSON
{
    "type": "drawScale",
    "aId": Number,
    "tId": Number,
    "scaleX": Number,
    "scaleY": Number,
    "posX" : Number,
    "posY" : Number
}
```