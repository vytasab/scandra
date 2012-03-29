package lt.node.scandra.pirkimai.util

import android.view.View
import android.app.{AlertDialog, Activity}
import android.widget.EditText
import android.content.DialogInterface

//-> http://stackoverflow.com/questions/4134117/edittext-on-a-popup-window
trait ScalaFunPro extends Activity {

  def inputString(title: String, message: String, okAction: (String) => Unit, cancelAction: () => Unit) {
    val alert: AlertDialog.Builder = new AlertDialog.Builder(this)

    if (title != null && title.length > 0) alert.setTitle(title)
    if (message != null && message.length > 0) alert.setMessage(message)

    val input: EditText = new EditText(this)
    alert.setView(input)
    input.setFocusable(true)
    input.setText(if (message != null && message.length > 0) message else "")

    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      def onClick(dialog: DialogInterface, whichButton: Int) {
        okAction(input.getText.toString)
      }
    })
    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      def onClick(dialog: DialogInterface, whichButton: Int) {
        cancelAction()
      }
    })
    alert.show()
  }


}


//-> http://robots.thoughtbot.com/post/5836463058/scala-a-better-java-for-android
//-> alternative: http://stackoverflow.com/questions/2984957/what-is-in-scala-android-jar?answertab=active#tab-top
trait FindView extends Activity {
  def findView[WidgetType](id: Int): WidgetType = findViewById(id).asInstanceOf[WidgetType]
}

object FindView {
  implicit def xxxxx(view: View) = new Xxxxx(view)
}

class Xxxxx(view: View) {
  def onClick(action: View => Any) {
    view.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        action(v)
      }
    })
  }
}

