package lt.node.scandra.pirkimai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu._
import android.view.{MenuItem, Menu, View}
import android.widget.{Toast, Button}
import android.content.res.Configuration
import java.io.File
import android.view.View._
import util.{ScalaFunPro, FindView, OrderPurchase, FileUtil}
import util.FindView._
import xml.XML
import android.util.Log

class Main extends Activity /*with OnClickListener*/ with OrderPurchase with ScalaFunPro with FindView {
  // TODO visur escap'inti: |"| |&| |<| |>|

  //private[this] val order, purchase, templates: Button = _
  //private[this] val purchase: Button = _
  private[this] var purchase: Button = null
  //private[this] val purchase: Button = _

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig);
    newConfig.orientation match {
      case Configuration.ORIENTATION_LANDSCAPE =>
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
      case Configuration.ORIENTATION_PORTRAIT =>
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
      case _ =>
        Toast.makeText(this, "NOT {landscape portrait} !!!", Toast.LENGTH_LONG).show()
    }
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    this.setContentView(R.layout.main)

    //order = findViewById(R.id.main_order_button).asInstanceOf[Button]
    //order setOnClickListener this
    purchase = findViewById(R.id.main_purchase_button).asInstanceOf[Button]
    //purchase setOnClickListener this

    findView[Button](R.id.main_order_button).onClick{view: View =>
      startActivity(new Intent(this, classOf[Order]).putExtra("createOrder", "yes"))
      //startActivity(new Intent(this, classOf[Groups]).putExtra("case", "Order"))
    }
    findView[Button](R.id.main_purchase_button).onClick{view: View =>
      startActivity(new Intent(this, classOf[Purchase]))
    }
    findView[Button](R.id.main_templates_button).onClick{view: View =>
      startActivity(new Intent(this, classOf[Groups]))
    }


    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
    val orderxmlFile = new File(dir, "orderxml.txt")
   orderxmlFile.exists() match {
      case false =>
        purchase.setVisibility(GONE)
      case true if (XML.loadFile(orderxmlFile)\"t").length > 0 =>
        purchase.setVisibility(VISIBLE)
      case _ =>
        purchase.setVisibility(GONE)
    }
    // TO-done-DO rodyti tik kai yra suformuotas 'orderxml.txt' failas
  }

  /*def onClick(view: View) {
    view match {
      case v if view equals order =>
        startActivity(new Intent(this, classOf[Order]).putExtra("createOrder", "yes"))
      case v if view equals purchase =>
        startActivity(new Intent(this, classOf[Purchase]))
      case _ =>
      //this.fi
    }
  }*/


  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.about).setIcon(android.R.drawable.ic_menu_info_details)
    menu.add(NONE, 1, 1, R.string.new_goods_type).setIcon(android.R.drawable.ic_menu_add /*ic_menu_compose*/)
    true
  }

  override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
    super.onMenuItemSelected(featureId, item)
    //setUomChoice(if (item.getItemId == 1) METRIC else ENGLISH)
    //val node: Node = XML.loadString(orderString)
    //val orderString: String =
    //FileUtil.readFileAsString(new File(FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName()), "orderxml.txt"))
    item.getItemId match {
      case 0 =>
        Toast.makeText(getApplicationContext(), "Apie ...", Toast.LENGTH_LONG).show()
      // TODO padaryti apk'o mini aprašėlį
      case 1 =>
        startActivity(new Intent(this, classOf[Groups]))
      // TODO padaryti funkcionalumą
      case _ =>
    }
    true
  }

  onStop()

  def tagclass: String = (TAG + " " + this.getClass.getSimpleName + " ")

  def logExtras(extras: scala.List[AnyRef], msg: String)  {
    extras.foreach(e => {
      val ee: String = e.asInstanceOf[String]
      val value =  if (getIntent.getStringExtra(ee) == null) "--null--" else getIntent.getStringExtra(ee)
      Log.v(tagclass + msg + " " + ee, value)
    })
  }
  def logExtras(msg: String)  {
    val extras: scala.List[AnyRef] = getIntent.getExtras.keySet().toArray.toList
    logExtras(extras/*.map(e => e.asInstanceOf[String])*/, msg)
  }

}

