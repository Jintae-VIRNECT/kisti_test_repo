import { mapGetters, mapActions } from 'vuex'
import MenuButton from 'MenuButton'
import ToolButton from 'ToolButton'
import ToolPicker from 'ToolPicker'

export default {
  components: {
    MenuButton,
    ToolButton,
    ToolPicker,
  },
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      mode: 'document',
    }
  },
  computed: {
    ...mapGetters(['view', 'viewAction', 'tools']),
  },
  methods: {
    ...mapActions(['setAction', 'setTool']),
  },
}
