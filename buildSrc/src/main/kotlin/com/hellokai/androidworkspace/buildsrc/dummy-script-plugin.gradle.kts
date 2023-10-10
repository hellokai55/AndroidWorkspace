/**
 * 脚本插件，注意不能使用package
 */

println("The dummy-script-plugin from ./buildSrc is applied.")

class DummyBinaryPluginInScript: Plugin<Project> {
    override fun apply(target: Project) { // CAN NOT USE `project` as the param name here.
        println("The DummyBinaryPluginInScript from ./buildSrc is applied with ${target.name}.")
    }
}
apply<DummyBinaryPluginInScript>()

