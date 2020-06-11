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
  data() {
    return {
      mode: 'document',
    }
  },
  computed: {
    ...mapGetters(['action', 'tools']),
  },
  methods: {
    ...mapActions(['setAction', 'setTool']),
  },
}
