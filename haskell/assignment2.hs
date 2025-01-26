-- Assignment 2: Pi approximations in Haskell
-- Justin Roderick
-- PID: 5239523
-- NID: ju136012
-- Haskell compiler: https://play.haskell.org/
-- This assignment implements 5 different formulas that result in pi approximations.

-- Short pi approximation using arctan
shortPi :: Double
shortPi = 4 * (4 * atan (1 / 5) - atan (1 / 239))

-- Simple pi approximation
-- 4 times the summation of (-1)^k+1 divided by 2k-1
simplePi :: Int -> Double
simplePi n = 4 * sum [((-1) ** fromIntegral (k + 1)) / (2 * fromIntegral k - 1) | k <- [1..n]]

-- Wells pi approximation
-- Summation of 1 divided by (2k-1)^2
-- Find the square root and multiply by 8 to get pi
wellsPi :: Int -> Double
wellsPi n = sqrt (8 * sum [1 / ((2 * fromIntegral k - 1) ** 2) | k <- [1..n]])

-- Euler pi approximation
-- sqrt of 6 times the summation of 1/k^2
eulerPi :: Int -> Double
eulerPi n = sqrt (6 * sum [1 / (fromIntegral k ^ 2) | k <- [1..n]])

-- Wallis formula approximation
-- 2 times the product of 2k^2 divided (2k-1)(2k+1)
wallisPi :: Int -> Double
wallisPi n = 2 * product [((2 * fromIntegral k) ^ 2) / ((2 * fromIntegral k - 1) * (2 * fromIntegral k + 1)) | k <- [1..n]]

-- Main function that prints the fomula and the given pi approximation
main :: IO ()
main = do
  putStrLn "Short Pi Approximation:"
  print (shortPi)
  putStrLn "Simple Pi Approximation:"
  print (simplePi 200)
  putStrLn "Wells Pi Approximation:"
  print (wellsPi 200)
  putStrLn "Euler Pi Approximation:"
  print (eulerPi 200)
  putStrLn "Wallis Pi Approximation:"
  print (wallisPi 200)

  -- The pi calculation method that gave the most percise output, outside of short pi, was Wells Pi with 3.140000704127709