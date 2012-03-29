package lt.node.scandra.pirkimai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View._
import util.{FileUtil, OrderPurchase}
import xml.{XML, Node}
import android.util.Log
import android.widget.{Toast, TextView, Button}

class PurchaseDecision extends Activity with OnClickListener with OrderPurchase {

  private var textField: TextView = _
  private var decisionDone: Button = _
  private var decisionPartiallyDone: Button = _
  private var decisionRevert: Button = _
  //private var decisionCancel: Button = _

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    this.setContentView(R.layout.purchase_decision)

    this.textField = findViewById(R.id.purchase_decision_item).asInstanceOf[TextView]
    this.textField.setText(getIntent.getStringExtra("selectedItem"))

    this.decisionDone = findViewById(R.id.purchase_decision_done_button).asInstanceOf[Button]
    this.decisionDone setOnClickListener this

    this.decisionPartiallyDone = findViewById(R.id.purchase_decision_partially_done_button).asInstanceOf[Button]
    this.decisionPartiallyDone setOnClickListener this

    this.decisionRevert = findViewById(R.id.purchase_decision_revert_button).asInstanceOf[Button]
    this.decisionRevert setOnClickListener this

    //this.decisionCancel = findViewById(R.id.purchase_decision_cancel_button).asInstanceOf[Button]
    //this.decisionCancel setOnClickListener this

    Log.v(this.TAG+" PurchaseDecision onCreate orderString", "|"+getIntent.getStringExtra("orderString")+"|")
    Log.v(this.TAG+" PurchaseDecision onCreate selectedItem", "|"+getIntent.getStringExtra("selectedItem")+"|")
    getStatus(getIntent.getStringExtra("orderString"), getIntent.getStringExtra("selectedItem")) match {
      case 2 =>
        this.decisionDone.setVisibility(VISIBLE)
        this.decisionPartiallyDone.setVisibility(VISIBLE)
        this.decisionRevert.setVisibility(GONE)
      case 1 =>
        this.decisionDone.setVisibility(VISIBLE)
        this.decisionPartiallyDone.setVisibility(GONE)
        this.decisionRevert.setVisibility(VISIBLE)
      case 0 =>
        this.decisionDone.setVisibility(GONE)
        this.decisionPartiallyDone.setVisibility(VISIBLE)
        this.decisionRevert.setVisibility(VISIBLE)
      case n =>
        Toast.makeText(getApplicationContext, "Netikėtas pirkimo statusas="+n, Toast.LENGTH_LONG).show()

    }

  }

  def onClick(view: View) {
    view match {
      case v if (v == decisionDone) || (v == decisionPartiallyDone) || (v == decisionRevert) =>
        val newPurchaseNode: Node = v match {
          case vv if (vv == decisionDone) =>
            Log.v(this.TAG+" PurchaseDecision", "onClick decisionDone ...")
            updateStatus(XML.loadString(getIntent.getStringExtra("orderString")),
              keyOfPurchaseItem(getIntent.getStringExtra("selectedItem")), 0)
          case vv if (v == decisionPartiallyDone) =>
            Log.v(this.TAG+" PurchaseDecision", "onClick decisionPartiallyDone ...")
            updateStatus(XML.loadString(getIntent.getStringExtra("orderString")),
              keyOfPurchaseItem(getIntent.getStringExtra("selectedItem")), 1)
          case vv if (v == decisionRevert) =>
            Log.v(this.TAG+" PurchaseDecision", "onClick decisionRevert ...")
            updateStatus(XML.loadString(getIntent.getStringExtra("orderString")),
              keyOfPurchaseItem(getIntent.getStringExtra("selectedItem")), 2)
        }
        Log.v(this.TAG+" PurchaseDecision key= ", keyOfPurchaseItem(getIntent.getStringExtra("selectedItem")))
        val purchaseIntent = new Intent(this, classOf[Purchase])
        purchaseIntent.putExtra("orderString", newPurchaseNode.buildString(true))
        Log.v(this.TAG+" PurchaseDecision newPurchaseNode=", newPurchaseNode.buildString(true))

        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        scala.xml.XML.save(dir+"/"+"orderxml.txt", newPurchaseNode, "UTF-8", true, null)

        startActivity(purchaseIntent)
        //Log.v(this.TAG+" PurchaseDecision", "4")
//      case v if v == decisionDone =>
//        Log.v(this.TAG+" PurchaseDecision", "onClick decisionDone ...")
//        val newPurchaseNode: Node = updateStatus(XML.loadString(getIntent().getStringExtra("orderString")),
//          keyOfPurchaseItem(getIntent().getStringExtra("selectedItem")), 0)
//        Log.v(this.TAG+" PurchaseDecision key= ", keyOfPurchaseItem(getIntent().getStringExtra("selectedItem")))
//        val purchaseIntent = new Intent(this, classOf[Purchase])
//        purchaseIntent.putExtra("orderString", newPurchaseNode.buildString(true))
//        Log.v(this.TAG+" PurchaseDecision newPurchaseNode=", newPurchaseNode.buildString(true))
//
//        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName());
//        scala.xml.XML.save(dir+"/"+"orderxml.txt", newPurchaseNode, "UTF-8", true, null)
//
//        startActivity(purchaseIntent)
//        //Log.v(this.TAG+" PurchaseDecision", "4")
//      case v if v == decisionPartiallyDone =>
//        //startActivity(new Intent(this, classOf[Purchase]))
//        Log.v(this.TAG+" PurchaseDecision", "onClick decisionPartiallyDone ...")
//        val newPurchaseNode: Node = updateStatus(XML.loadString(getIntent().getStringExtra("orderString")),
//          keyOfPurchaseItem(getIntent().getStringExtra("selectedItem")), 1)
//        Log.v(this.TAG+" PurchaseDecision key= ", keyOfPurchaseItem(getIntent().getStringExtra("selectedItem")))
//        val purchaseIntent = new Intent(this, classOf[Purchase])
//        purchaseIntent.putExtra("orderString", newPurchaseNode.buildString(true))
//        Log.v(this.TAG+" PurchaseDecision newPurchaseNode=", newPurchaseNode.buildString(true))
//
//        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName());
//        scala.xml.XML.save(dir+"/"+"orderxml.txt", newPurchaseNode, "UTF-8", true, null)
//
//        startActivity(purchaseIntent)
//      //Log.v(this.TAG+" PurchaseDecision", "4")
      case _ =>
        Toast.makeText(getApplicationContext, "Čia bus kuriamas naujas prekės tipas", Toast.LENGTH_LONG).show()
        startActivity(new Intent(this, classOf[Purchase]))
    }
  }

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

