int a = 0; /* OK */

int myFunction()
{
  while (a > 0) /* NOK */
  {
    int b = 10; /* OK */
    
    a = 150; /* NOK */
  }
  
  return 42; /* OK */
}
