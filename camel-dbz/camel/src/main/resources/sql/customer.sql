-- this is a comment
select C.custId, C.name
from braindose.customer C, braindose.customerOrders O
where
  O.orderid = :#${exchangeProperty.orderId}
  and C.custid = O.custid