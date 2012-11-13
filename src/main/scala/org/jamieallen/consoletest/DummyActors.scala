package org.jamieallen.consoletest

import akka.util.duration._
import akka.actor._
import akka.dispatch.ExecutionContext
import akka.dispatch.Promise
import java.util.concurrent.TimeoutException
import akka.dispatch.Await
import akka.pattern.pipe

case class GetCustomerAccountBalances(id: Long)
case class AccountBalances(
  val checking: Option[List[(Long, BigDecimal)]],
  val savings: Option[List[(Long, BigDecimal)]],
  val moneyMarket: Option[List[(Long, BigDecimal)]])
case class CheckingAccountBalances(
  val balances: Option[List[(Long, BigDecimal)]])
case class SavingsAccountBalances(
  val balances: Option[List[(Long, BigDecimal)]])
case class MoneyMarketAccountBalances(
  val balances: Option[List[(Long, BigDecimal)]])

class SavingsAccountProxy extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      sender ! SavingsAccountBalances(Some(List((1, 150000), (2, 29000))))
  }
}
class CheckingAccountProxy extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      sender ! CheckingAccountBalances(Some(List((3, 15000))))
  }
}
class MoneyMarketAccountsProxy extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      sender ! MoneyMarketAccountBalances(Some(List()))
  }
}

class AccountBalanceResponseHandler(savingsAccounts: ActorRef, checkingAccounts: ActorRef,
  moneyMarketAccounts: ActorRef, originalSender: ActorRef) extends Actor {

  import context.dispatcher
  val promisedResult = Promise[AccountBalances]()
  var checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
  def receive = {
    case CheckingAccountBalances(balances) =>
      checkingBalances = balances
      collectBalances
    case SavingsAccountBalances(balances) =>
      savingsBalances = balances
      collectBalances
    case MoneyMarketAccountBalances(balances) =>
      mmBalances = balances
      collectBalances
  }

  def collectBalances = (checkingBalances, savingsBalances, mmBalances) match {
    case (Some(c), Some(s), Some(m)) =>
      if (promisedResult.tryComplete(Right(AccountBalances(checkingBalances, savingsBalances, mmBalances))))
        sendResults
    case _ =>
  }

  def sendResults = {
    promisedResult.future.map(x => x) pipeTo originalSender
    context.system.stop(self)
  }

  context.system.scheduler.scheduleOnce(250 milliseconds) {
    if (promisedResult.tryComplete(Left(new TimeoutException)))
      sendResults
  }
}

class AccountBalanceRetriever(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id) =>
      val originalSender = sender
      val handler = context.actorOf(Props(new AccountBalanceResponseHandler(savingsAccounts, checkingAccounts, moneyMarketAccounts, originalSender)))
      savingsAccounts.tell(GetCustomerAccountBalances(id), handler)
      checkingAccounts.tell(GetCustomerAccountBalances(id), handler)
      moneyMarketAccounts.tell(GetCustomerAccountBalances(id), handler)
  }
}

object Bootstrap extends App {
  val system = ActorSystem("console-test")
  val savingsAccountProxy = system.actorOf(Props[SavingsAccountProxy])
  val checkingAccountProxy = system.actorOf(Props[CheckingAccountProxy])
  val moneyMarketAccountProxy = system.actorOf(Props[MoneyMarketAccountsProxy])
  val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetriever(savingsAccountProxy, checkingAccountProxy, moneyMarketAccountProxy)))

  system.actorOf(Props(new Actor() {
    def receive = {
      case AccountBalances(cBalances, sBalances, mmBalances) =>
        cBalances map (_ map (x => println("Checking Balance: %s".format(x._2))))
        sBalances map (_ map (x => println("Saving Balance: %s".format(x._2))))
        mmBalances map (_ map (x => println("MM Balance: %s".format(x._2))))
        context.system.shutdown
    }

    accountBalanceRetriever.tell(GetCustomerAccountBalances(1L), self)
  }))
}
