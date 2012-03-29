package lt.node.scandra.pirkimai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.Menu._
import android.view.{MenuItem, Menu, View}
import android.widget.{Toast, Button}
import android.util.Log
import util.OrderPurchase
import android.app.Activity._

class OrderNewOrResume extends Activity with OnClickListener with OrderPurchase {

  private[this] var orderNewBtn: Button = _
  private[this] var orderResumeBtn: Button = _

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    //logExtras("onCreate ------ ")
    this.setContentView(R.layout.order_new_or_resume)

    this.orderNewBtn = findViewById(R.id.order_new_button).asInstanceOf[Button]
    this.orderNewBtn setOnClickListener this

    this.orderResumeBtn = findViewById(R.id.order_resume_button).asInstanceOf[Button]
    this.orderResumeBtn setOnClickListener this
  }

  def onClick(view: View) {
    //val oIntent = new Intent(this, classOf[Order])
    view match {
      case v if view equals orderNewBtn =>
        /*startActivity(new Intent(this, classOf[Order]).
          putExtra("newOrder", "yes").
          putExtra("groupId", getIntent.getStringExtra("groupId"))
        )*/
        startActivity(new Intent(this, classOf[Groups]).
          putExtra("case", "Order"))

      case v if view equals orderResumeBtn =>
        startActivity(new Intent(this, classOf[Order]).
          putExtra("resumeOrder", "yes").
          putExtra("back2PreOrder", "yes"))
      case _ =>
    }
  }


  /*override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.new_goods_type)
    //    menu.add(NONE, 1, 1, R.string.results_done)
    //    menu.add(NONE, 2, 2, R.string.results_partially_done)
    true
  }*/

  /*override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
    super.onMenuItemSelected(featureId, item)
    //setUomChoice(if (item.getItemId == 1) METRIC else ENGLISH)
    //val node: Node = XML.loadString(orderString)
    //val orderString: String =
    //FileUtil.readFileAsString(new File(FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName()), "orderxml.txt"))
    item.getItemId match {
      case 0 =>
        Toast.makeText(getApplicationContext(), "Čia bus kuriamas naujas prekės tipas", Toast.LENGTH_LONG).show()
      //        showOrderItems(orderString)
      //      case 1 =>
      //        showOrderItemsDone(orderString)
      //      case 2 =>
      //        showOrderItemsPartiallyDone(orderString)
      //      case _ =>
    }
    true
  }*/

  def tagclass: String = (TAG + " " + this.getClass.getSimpleName + " ")

  def logExtras(extras: scala.List[AnyRef], msg: String)  {
    extras.foreach(e => {
      val ee: String = e.asInstanceOf[String]
      val value =  if (getIntent.getStringExtra(ee) == null) "--null--" else getIntent.getStringExtra(ee)
      Log.v(tagclass + msg + " " + ee, value)
    })
  }
  def logExtras(msg: String)  {
    getIntent
    getIntent.getExtras
    getIntent.getExtras.isEmpty/* .keySet()*/ match {
      case true =>
      case false =>
        val extras: scala.List[AnyRef] = getIntent.getExtras.keySet().toArray.toList
        logExtras(extras, msg)
      //case ks if ks.size==0 =>
      //case ks => logExtras(ks.toArray.toList, msg)
      //case _ =>
    }
    //val extras: scala.List[AnyRef] = getIntent.getExtras.keySet().toArray.toList
    //logExtras(extras/*.map(e => e.asInstanceOf[String])*/, msg)
  }


}

