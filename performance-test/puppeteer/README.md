
## Test

video element
```bash
npm run video
```

2d-canvas
```bash
npm run 2d
```

3d-canvas (use three.js)
```bash
npm run 3d
```

## Docker

```bash
docker build --rm --tag test -f docker/Dockerfile .
docker run -it --rm --name test test
```