<template>
   <div class="Widget">

        <el-tabs v-model="activeLabelName">
            <el-tab-pane v-for="(item, index) in labelStructure" :key="`${index}-${item.value}`" :label="item.label" :name="`${index}-${item.value}`">
                <component :is="`${item.value}`" :renderList="item.renderList" />
            </el-tab-pane>
        </el-tabs>

    </div>
</template>

<script>
import baseElement from '../Widget/baseElement/index.vue'
export default {
    name:'Widget',
    components:{
        baseElement
    },

  data() {
      return {
          //activeLabelName = `${0}-${this.labelStructure[0].value}`,

           labelStructure:[
               {
                    label: '基础元件',
                    value: 'baseElement',
                    renderList: [
                        {
                            name: '基础几何模型',
                            icon: 'el-icon-camera-solid',
                            children: [
                                { type: 'lonelyPlanet', name: '孤独患者' },
                                { type: 'bezierCurve', name: '古风纯享'},
                                { type: 'ethereaTone', name: '空灵轻音'},
                                { type: 'electronicMusic', name: '激燃电音'},
                                { type: 'blastSubwoofer', name: '爆炸重低音'}



                                ]
                        },
                        {
                            name: '3D音乐模型',
                            icon: 'el-icon-sunrise-1',
                            children: [
                                { type: 'BarModel', name: '迷幻电子管' },
                                { type: 'LineModel', name: '动感光波' },

                            ]
                        }
                    ]
               },
                {
                    label: '天空盒库',
                    value: 'baseElement',
                    renderList: [
                        {
                            name: '背景图',
                            icon: 'el-icon-s-custom',
                            children: [
                               { type: 'BlueRed',name: '模板1'},
                               { type: 'DeepBlack',name: '模板2'},
                               { type: 'Galaxy',name: '模板3'},
                               { type: 'Green',name: '模板4'},
                               { type: 'NuminousGlow',name: '模板5'},
                               { type: 'OrangeBlue',name: '模板6'},
                               { type: 'Red',name: '模板7'},
                               { type: 'Starless',name: '模板8'},
                               { type: 'VortexCloud',name: '模板9'}

                            ]
                        }
                    ]
                }
           ],
            activeLabelName:`0-baseElement`
      }
  },


        // activeLabelName = `${0}-${this.labelStructure[0].value}`,


}
</script>

<style lang="scss" scoped>
.Widget {
    color: white;
    ::v-deep .el-tabs {
        margin: 8px;
        height: calc(100% - 16px);
        display: flex;
        flex-direction: column;
        .el-tabs__header {
            margin: 0;
            .el-tabs__nav {
                width: 100%;
                display: flex;
                .el-tabs__item {
                    height: 30px;
                    line-height: 30px;
                    flex: 1;
                    padding: 0;
                    text-align: center;
                    font-size: 12px;
                    background: #281928;
                    color: white;
                    &:nth-of-type(n + 3) {
                        border-left: 1px solid black;
                    }
                }
                .is-active {
                    color: #f300ff !important;
                }
                .el-tabs__active-bar {
                    background: rgba(243, 0, 255, 0.65);
                }
            }
        }
        .el-tabs__content {
            flex: 1;
            margin-top: 8px;
            overflow: hidden;
            .el-tab-pane {
                height: 100%;
                width: 100%;
            }
        }
    }
}
</style>
