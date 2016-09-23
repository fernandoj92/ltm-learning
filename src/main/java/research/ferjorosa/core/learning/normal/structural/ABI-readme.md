The ABI algorithm is an approximation of the Bridged Islands algorithm proposed by Liu et.al in their article:

* Liu, T., Zhang, N. L., Poon, K. M., Liu, H., & Wang, Y. (2012, September). A novel ltm-based method for multi-partition clustering. In 6th European Workshop on Probabilistic Graphical Models (pp. 203-210).

Later extended in this article:

* Liu, T. F., Zhang, N. L., Chen, P., Liu, A. H., Poon, L. K., & Wang, Y. (2015). Greedy learning of latent tree models for multidimensional clustering. Machine Learning, 98(1-2), 301-330.

This algorithm learns both the structure and the parameters of a Bayesian network. Its composed of 4 stages
1. Calculate the sibling clusters.
2. Generate a LCM (a LCM is a LTM with only one Latent variable) from each sibling cluster by assigning a Latent
variable on top of the chosen observed variables.
3. Learn each LCM's parameters, then repeatedly consider to increase the cardinality of the LV, stopping when the
used score decreases (BIC, BDe, AIC,etc., depending on the parameter Learning algorithm used).
4. Determine the connections among the latent variables so that they form a tree (LTM).
5. Refine the model.
6. TODO: Determine possible connections between the observed variables.